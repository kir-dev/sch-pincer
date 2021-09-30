package hu.kirdev.schpincer.web.component

import com.fasterxml.jackson.databind.ObjectMapper
import hu.kirdev.schpincer.dto.OrderDetailsDto
import hu.kirdev.schpincer.model.CardType
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OrderEntity
import hu.kirdev.schpincer.model.UserEntity
import java.io.IOException
import java.util.*

const val ITEM_COUNT_COMPONENT_TYPE = "ITEM_COUNT"

enum class CustomComponentType {
    EXTRA_SELECT {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            return ccm.prices.get(cca.selected.get(0))
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val list: MutableList<String> = ArrayList()
            list.add(ccm.aliases.get(cca.selected.get(0)))
            return list
        }
    },

    EXTRA_CHECKBOX {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            var result = 0
            for (index in cca.selected) result += ccm.prices[index]
            return result
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val result: MutableList<String> = ArrayList()
            for (index in cca.selected) result.add(ccm.aliases[index])
            return result
        }
    },

    PIZZASCH_SELECT {

        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            return ccm.prices[cca.selected[0]]
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
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
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            var result = 0
            for (index in cca.selected)
                result += ccm.prices[index]
            return result
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val result: MutableList<String> = ArrayList(ccm.values)
            for (index in cca.selected)
                result.removeIf { x: String -> x == ccm.aliases[index] }
            return result
        }
    },

    DO_SELECT {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            if (user.cardType == CardType.DO)
                return ccm.prices.get(cca.selected.get(0))
            return 0
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val list: MutableList<String> = ArrayList()
            if (user.cardType == CardType.DO)
                list.add(ccm.aliases.get(cca.selected.get(0)))
            return list
        }
    },

    KB_SELECT {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            if (user.cardType == CardType.KB)
                return ccm.prices.get(cca.selected.get(0))
            return 0
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val list: MutableList<String> = ArrayList()
            if (user.cardType == CardType.KB)
                list.add(ccm.aliases.get(cca.selected.get(0)))
            return list
        }
    },

    AB_SELECT {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            if (user.cardType == CardType.AB)
                return ccm.prices.get(cca.selected.get(0))
            return 0
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val list: MutableList<String> = ArrayList()
            if (user.cardType == CardType.AB)
                list.add(ccm.aliases.get(cca.selected.get(0)))
            return list
        }
    },

    AB_KB_SELECT {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            if (user.cardType == CardType.KB || user.cardType == CardType.AB)
                return ccm.prices.get(cca.selected.get(0))
            return 0
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val list: MutableList<String> = ArrayList()
            if (user.cardType == CardType.KB || user.cardType == CardType.AB)
                list.add(ccm.aliases.get(cca.selected.get(0)))
            return list
        }
    },

    DO_CHECKBOX {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            var result = 0
            if (user.cardType == CardType.DO)
                for (index in cca.selected)
                    result += ccm.prices[index]
            return result
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val result: MutableList<String> = ArrayList()
            if (user.cardType == CardType.DO)
                for (index in cca.selected)
                    result.add(ccm.aliases[index])
            return result
        }
    },

    KB_CHECKBOX {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            var result = 0
            if (user.cardType == CardType.KB)
                for (index in cca.selected)
                    result += ccm.prices[index]
            return result
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val result: MutableList<String> = ArrayList()
            if (user.cardType == CardType.KB)
                for (index in cca.selected)
                    result.add(ccm.aliases[index])
            return result
        }
    },

    AB_CHECKBOX {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            var result = 0
            if (user.cardType == CardType.AB)
                for (index in cca.selected)
                    result += ccm.prices[index]
            return result
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val result: MutableList<String> = ArrayList()
            if (user.cardType == CardType.AB)
                for (index in cca.selected)
                    result.add(ccm.aliases[index])
            return result
        }
    },

    AB_KB_CHECKBOX {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            var result = 0
            if (user.cardType == CardType.KB || user.cardType == CardType.AB)
                for (index in cca.selected)
                    result += ccm.prices[index]
            return result
        }

        override fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> {
            val result: MutableList<String> = ArrayList()
            if (user.cardType == CardType.KB || user.cardType == CardType.AB)
                for (index in cca.selected)
                    result.add(ccm.aliases[index])
            return result
        }
    },

    UNKNOWN {
        override fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int {
            throw RuntimeException("Unknown type: " + cca.type)
        }
    }
    ;

    open fun processPrices(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): Int = 0

    open fun processMessage(cca: CustomComponentAnswer, oe: OrderEntity, ccm: CustomComponentModel, user: UserEntity): List<String> = listOf()

    open fun isExtra(cca: CustomComponentAnswer): Boolean = false

}

private val mapper = ObjectMapper()
private val types: Map<String, CustomComponentType> = CustomComponentType.values().associateBy({ it.name }, { it })

@Throws(IOException::class)
fun calculateExtra(detailsJson: String, order: OrderEntity, ie: ItemEntity, user: UserEntity): OrderDetailsDto {
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
                .processPrices(answer, order, mapped[answer.name]!!, user)
        extraString.add((types[answer.type] ?: CustomComponentType.UNKNOWN)
                .processMessage(answer, order, mapped[answer.name]!!, user)
                .joinToString(", "))
        if ((types[answer.type] ?: CustomComponentType.UNKNOWN).isExtra(answer)) order.extraTag = true
    }
    order.price = (if (ie.discountPrice == 0) ie.price else ie.discountPrice) + extraPrice
    order.extra = extraString.filter { Objects.nonNull(it) }.joinToString("; ")
    return details
}
