package hu.kirdev.schpincer.web.component

import com.fasterxml.jackson.databind.ObjectMapper
import hu.kirdev.schpincer.dto.OrderDetailsDto
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OrderEntity
import java.io.IOException
import java.util.*

const val ITEM_COUNT_COMPONENT_TYPE = "ITEM_COUNT"

enum class CustomComponentType {
    EXTRA_SELECT {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): Int {
            return ccm.prices.get(cca.selected.get(0))
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): List<String> {
            val list: MutableList<String> = ArrayList()
            list.add(ccm.aliases.get(cca.selected.get(0)))
            return list
        }
    },

    EXTRA_CHECKBOX {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): Int {
            var result = 0
            for (index in cca.selected) result += ccm.prices[index]
            return result
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): List<String> {
            val result: MutableList<String> = ArrayList()
            for (index in cca.selected) result.add(ccm.aliases[index])
            return result
        }
    },

    PIZZASCH_SELECT {

        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): Int {
            return ccm.prices[cca.selected[0]]
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): List<String> {
            val list: MutableList<String> = ArrayList()
            list.add(ccm.aliases[cca.selected[0]])
            return list
        }

        override fun isExtra(cca: CustomComponentAnswer): Boolean {
            return cca.selected[0] > 0
        }
    },

    LANGOSCH_IMAGEDRAWER,

    AMERICANO_SELECT {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): Int {
            var result = 0
            for (index in cca.selected)
                result += ccm.prices[index]
            return result
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): List<String> {
            val result: MutableList<String> = ArrayList(ccm.values)
            for (index in cca.selected)
                result.removeIf { x: String -> x == ccm.aliases[index] }
            return result
        }
    },

    UNKNOWN {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): Int {
            throw RuntimeException("Unknown type: " + cca.type)
        }
    }
    ;

    open fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): Int = 0

    open fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel): List<String> = listOf()

    open fun isExtra(cca: CustomComponentAnswer): Boolean = false

}

private val mapper = ObjectMapper()
private val types: Map<String, CustomComponentType> = CustomComponentType.values().associateBy({ it.name }, { it })

@Throws(IOException::class)
fun calculateExtra(detailsJson: String, order: OrderEntity, ie: ItemEntity): OrderDetailsDto {
    val details = OrderDetailsDto()
    val answers = mapper.readValue(detailsJson.toByteArray(), CustomComponentAnswerList::class.java)
    val models = mapper.readValue(("{\"models\":${ie.detailsConfigJson}}").toByteArray(), CustomComponentModelList::class.java)
    val mapped: Map<String, CustomComponentModel> = models.models.associateBy({ it.name }, { it })

    for (model in models.models) {
        if (model.type == ITEM_COUNT_COMPONENT_TYPE) {
            details.minCount = model.min
            details.maxCount = model.max
        }
    }
    var extraPrice = 0
    val extraString: MutableList<String> = ArrayList()
    for (answer in answers.answers) {
        extraPrice += (types[answer.type] ?: CustomComponentType.UNKNOWN)
                .processPrices(answer, order, mapped[answer.name]!!)
        extraString.add((types[answer.type] ?: CustomComponentType.UNKNOWN)
                .processMessage(answer, order, mapped[answer.name]!!)
                .joinToString(", "))
        if ((types[answer.type] ?: CustomComponentType.UNKNOWN).isExtra(answer)) order.extraTag = true
    }
    order.price = (if (ie.discountPrice == 0) ie.price else ie.discountPrice) + extraPrice
    order.extra = extraString.filter { Objects.nonNull(it) }.joinToString("; ")
    return details
}
