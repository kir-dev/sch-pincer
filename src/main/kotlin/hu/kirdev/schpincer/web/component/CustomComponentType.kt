package hu.kirdev.schpincer.web.component

import hu.kirdev.schpincer.dto.OrderDetailsDto
import hu.kirdev.schpincer.model.CardType
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OrderEntity
import tools.jackson.databind.ObjectMapper
import java.io.IOException
import java.util.*

const val ITEM_COUNT_COMPONENT_TYPE = "ITEM_COUNT"

enum class CustomComponentType {
    EXTRA_SELECT {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            return ccm.prices?.get(cca.selected[0]) ?: 0
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val list: MutableList<String> = ArrayList()
            list.add(ccm.aliases?.get(cca.selected[0]) ?: "")
            return list
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            return mapOf(
                Pair("${cca.name}- ${ccm.values?.get(cca.selected[0])}", ccm.prices?.get(cca.selected[0]) ?: 0)
            )
        }
    },

    EXTRA_CHECKBOX {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            var result = 0
            for (index in cca.selected)
                result += ccm.prices?.get(index) ?: 0
            return result
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val result: MutableList<String> = ArrayList()
            for (index in cca.selected)
                result.add(ccm.aliases?.get(index) ?: "")
            return result
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            val result = mutableMapOf<String, Int>()
            for (index in cca.selected)
                result["${cca.name}- ${ccm.values?.get(index)}"] = ccm.prices?.get(index) ?: 0
            return result
        }
    },

    PIZZASCH_SELECT {

        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            return ccm.prices?.get(cca.selected[0]) ?: 0
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val list: MutableList<String> = ArrayList()
            list.add(ccm.aliases?.get(cca.selected[0]) ?: "")
            return list
        }

        override fun isExtra(cca: CustomComponentAnswer): Boolean {
            return cca.selected[0] > 0
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            return mapOf(
                Pair("${cca.name}- ${ccm.values?.get(cca.selected[0])}", ccm.prices?.get(cca.selected[0]) ?: 0)
            )
        }
    },

    LANGOSCH_IMAGEDRAWER,

    AMERICANO_SELECT {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            var result = 0
            for (index in cca.selected)
                result += ccm.prices?.get(index) ?: 0
            return result
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val result: MutableList<String> = ccm.values?.toMutableList() ?: mutableListOf()
            for (index in cca.selected)
                result.removeIf { x: String -> x == ccm.aliases?.get(index) }
            return result
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            val result = mutableMapOf<String, Int>()
            for (index in cca.selected)
                result["${cca.name}- ${ccm.values?.get(index)}"] = ccm.prices?.get(index) ?: 0
            return result
        }
    },

    DO_SELECT {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            if (cardType == CardType.DO)
                return ccm.prices?.get(cca.selected[0]) ?: 0
            return 0
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val list: MutableList<String> = ArrayList()
            if (cardType == CardType.DO)
                list.add(ccm.aliases?.get(cca.selected[0]) ?: "")
            return list
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            if (cardType == CardType.DO)
                return mapOf(
                    Pair("${cca.name}- ${ccm.values?.get(cca.selected[0])}", ccm.prices?.get(cca.selected[0]) ?: 0)
                )
            return mapOf()
        }
    },

    KB_SELECT {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            if (cardType == CardType.KB)
                return ccm.prices?.get(cca.selected[0]) ?: 0
            return 0
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val list: MutableList<String> = ArrayList()
            if (cardType == CardType.KB)
                list.add(ccm.aliases?.get(cca.selected[0]) ?: "")
            return list
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            if (cardType == CardType.KB)
                return mapOf(
                    Pair("${cca.name}- ${ccm.values?.get(cca.selected[0])}", ccm.prices?.get(cca.selected[0]) ?: 0)
                )
            return mapOf()
        }
    },

    AB_SELECT {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            if (cardType == CardType.AB)
                return ccm.prices?.get(cca.selected[0]) ?: 0
            return 0
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val list: MutableList<String> = ArrayList()
            if (cardType == CardType.AB)
                list.add(ccm.aliases?.get(cca.selected[0]) ?: "")
            return list
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            if (cardType == CardType.AB)
                return mapOf(
                    Pair("${cca.name}- ${ccm.values?.get(cca.selected[0])}", ccm.prices?.get(cca.selected[0]) ?: 0)
                )
            return mapOf()
        }
    },

    AB_KB_SELECT {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            if (cardType == CardType.KB || cardType == CardType.AB)
                return ccm.prices?.get(cca.selected[0]) ?: 0
            return 0
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val list: MutableList<String> = ArrayList()
            if (cardType == CardType.KB || cardType == CardType.AB)
                list.add(ccm.aliases?.get(cca.selected[0]) ?: "")
            return list
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            val result = mutableMapOf<String, Int>()
            if (cardType == CardType.KB || cardType == CardType.AB)
                result["${cca.name}- ${ccm.values?.get(cca.selected[0])}"] = ccm.prices?.get(cca.selected[0]) ?: 0
            return result
        }
    },

    DO_CHECKBOX {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            var result = 0
            if (cardType == CardType.DO)
                for (index in cca.selected)
                    result += ccm.prices?.get(index) ?: 0
            return result
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val result: MutableList<String> = ArrayList()
            if (cardType == CardType.DO)
                for (index in cca.selected)
                    result.add(ccm.aliases?.get(index) ?: "")
            return result
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            val result = mutableMapOf<String, Int>()
            if (cardType == CardType.DO)
                for (index in cca.selected)
                    result["${cca.name}- ${ccm.values?.get(index)}"] = ccm.prices?.get(index) ?: 0
            return result
        }
    },

    KB_CHECKBOX {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            var result = 0
            if (cardType == CardType.KB)
                for (index in cca.selected)
                    result += ccm.prices?.get(index) ?: 0
            return result
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val result: MutableList<String> = ArrayList()
            if (cardType == CardType.KB)
                for (index in cca.selected)
                    result.add(ccm.aliases?.get(index) ?: "")
            return result
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            val result = mutableMapOf<String, Int>()
            if (cardType == CardType.KB)
                for (index in cca.selected)
                    result["${cca.name}- ${ccm.values?.get(index)}"] = ccm.prices?.get(index) ?: 0
            return result
        }
    },

    AB_CHECKBOX {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            var result = 0
            if (cardType == CardType.AB)
                for (index in cca.selected)
                    result += ccm.prices?.get(index) ?: 0
            return result
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val result: MutableList<String> = ArrayList()
            if (cardType == CardType.AB)
                for (index in cca.selected)
                    result.add(ccm.aliases?.get(index) ?: "")
            return result
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            val result = mutableMapOf<String, Int>()
            if (cardType == CardType.AB)
                for (index in cca.selected)
                    result["${cca.name}- ${ccm.values?.get(index)}"] = ccm.prices?.get(index) ?: 0
            return result
        }
    },

    AB_KB_CHECKBOX {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            var result = 0
            if (cardType == CardType.KB || cardType == CardType.AB)
                for (index in cca.selected)
                    result += ccm.prices?.get(index) ?: 0
            return result
        }

        override fun processMessage(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): List<String> {
            val result: MutableList<String> = ArrayList()
            if (cardType == CardType.KB || cardType == CardType.AB)
                for (index in cca.selected)
                    result.add(ccm.aliases?.get(index) ?: "")
            return result
        }

        override fun generatePriceBreakdown(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Map<String, Int> {
            val result = mutableMapOf<String, Int>()
            if (cardType == CardType.KB || cardType == CardType.AB)
                for (index in cca.selected)
                    result["${cca.name}- ${ccm.values?.get(index)}"] = ccm.prices?.get(index) ?: 0
            return result
        }
    },

    UNKNOWN {
        override fun processPrices(
            cca: CustomComponentAnswer,
            oe: OrderEntity,
            ccm: CustomComponentModel,
            cardType: CardType
        ): Int {
            throw RuntimeException("Unknown type: " + cca.type)
        }
    }
    ;

    open fun processPrices(
        cca: CustomComponentAnswer,
        oe: OrderEntity,
        ccm: CustomComponentModel,
        cardType: CardType
    ): Int = 0

    open fun processMessage(
        cca: CustomComponentAnswer,
        oe: OrderEntity,
        ccm: CustomComponentModel,
        cardType: CardType
    ): List<String> = listOf()

    open fun isExtra(cca: CustomComponentAnswer): Boolean = false

    open fun generatePriceBreakdown(
        cca: CustomComponentAnswer,
        oe: OrderEntity,
        ccm: CustomComponentModel,
        cardType: CardType
    ): Map<String, Int> = mapOf()

}

private val mapper = ObjectMapper()
private val types: Map<String, CustomComponentType> = CustomComponentType.entries.associateBy({ it.name }, { it })

@Throws(IOException::class)
fun calculateExtra(detailsJson: String, order: OrderEntity, ie: ItemEntity, cardType: CardType): OrderDetailsDto {
    val details = OrderDetailsDto()
    val answers = mapper.readValue(detailsJson.toByteArray(), CustomComponentAnswerList::class.java)
    val models =
        mapper.readValue(("{\"models\":${ie.detailsConfigJson}}").toByteArray(), CustomComponentModelList::class.java)
    val mapped: Map<String, CustomComponentModel> = models.models.associateBy({ it.name ?: "" }, { it })

    for (model in models.models) {
        if (model.type == ITEM_COUNT_COMPONENT_TYPE) {
            details.minCount = model.min ?: 0
            details.maxCount = model.max ?: 0
        }
    }
    var extraPrice = 0
    val extraString: MutableList<String> = ArrayList()
    for (answer in answers.answers) {
        extraPrice += (types[answer.type] ?: CustomComponentType.UNKNOWN)
            .processPrices(answer, order, mapped[answer.name]!!, cardType)
        extraString.add(
            (types[answer.type] ?: CustomComponentType.UNKNOWN)
                .processMessage(answer, order, mapped[answer.name]!!, cardType)
                .joinToString(", ")
        )
        if ((types[answer.type] ?: CustomComponentType.UNKNOWN).isExtra(answer)) order.extraTag = true
    }
    order.price = (if (ie.discountPrice == 0) ie.price else ie.discountPrice) + extraPrice
    order.extra = extraString.filter { Objects.nonNull(it) }.joinToString("; ")
    return details
}
