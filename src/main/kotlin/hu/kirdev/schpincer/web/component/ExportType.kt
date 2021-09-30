package hu.kirdev.schpincer.web.component

import hu.kirdev.schpincer.model.OrderEntity
import hu.kirdev.schpincer.service.OrderStrategy

const val TIME_WINDOW_HEADER = "IDÖSÁV"
const val PRODUCT_HEADER = "TERMÉK"
const val COMMENT_HEADER = "MEGJEGYZÉS"

enum class ExportType(
        val isPortrait: Boolean,
        val displayName: String,
        val orderByFunction: String,
        val header: List<String>,
        val widths: IntArray,
        val fields: List<(OrderEntity) -> String>,
        val fontSize: Float = 10.0f,
        val denormalizeOrders: Boolean = false
) {

    DEFAULT(false, "Default", OrderStrategy.ORDER_GROUPED.representation,
            listOf("ID", "NÉV", TIME_WINDOW_HEADER, PRODUCT_HEADER, "EXTRA", COMMENT_HEADER, "ÁR"), intArrayOf(3, 8, 5, 10, 5, 10, 4),
            listOf<(OrderEntity) -> String>(
                    { it.artificialTransientId.toString() },
                    { it.userName },
                    { it.intervalMessage },
                    { it.name },
                    { it.extra },
                    { it.comment },
                    { it.price.toString() }
            )),
    DZSAJROSZ(false, "Dzsájrosz", OrderStrategy.ORDER_GROUPED.representation,
            listOf("ID", "NÉV", "SZOBA", PRODUCT_HEADER, "HAGYMA", "ÖNTET", "SAJT", "EXTRA", COMMENT_HEADER, "ÁR"), intArrayOf(3, 8, 5, 10, 8, 10, 8, 10, 20, 4),
            listOf<(OrderEntity) -> String>(
                    { it.artificialTransientId.toString() },
                    { it.userName },
                    { it.room.replace("SCH ", "") },
                    { it.name },
                    { order: OrderEntity -> order.extra.split("; ".toRegex(), 2).toTypedArray()[0] },
                    { order: OrderEntity -> if (order.extra.indexOf(';') != -1) order.extra.split(";".toRegex(), 3).toTypedArray()[1] else "-" },
                    { order: OrderEntity -> if (order.extra.indexOf(';') != order.extra.lastIndexOf(';')) order.extra.split("; ".toRegex(), 4).toTypedArray()[2] else "-" },
                    { order: OrderEntity -> if (order.extra.indexOf(';') != order.extra.lastIndexOf(';')) order.extra.split("; ".toRegex(), 5).toTypedArray()[3] else "-" },
                    { it.comment },
                    { it.price.toString() }
            )),
    AMERICANO(false, "Americano", OrderStrategy.ORDER_GROUPED.representation,
            listOf("ID", TIME_WINDOW_HEADER, "NÉV", "SZOBA", PRODUCT_HEADER, "EXTRA", COMMENT_HEADER, "ÁR"), intArrayOf(2, 4, 5, 2, 3, 7, 12, 2),
            listOf<(OrderEntity) -> String>(
                    { it.artificialTransientId.toString() },
                    { it.intervalMessage },
                    { it.userName },
                    { it.room },
                    { it.compactName },
                    { it.extra },
                    { it.comment },
                    { it.price.toString() }
            )),
    PIZZASCH(false, "Pizzasch", OrderStrategy.ORDER_GROUPED.representation,
            listOf("ID", "NÉV", "SZOBA", TIME_WINDOW_HEADER, PRODUCT_HEADER, COMMENT_HEADER, "ÁR"), intArrayOf(3, 8, 4, 5, 10, 10, 4),
            listOf<(OrderEntity) -> String>(
                    { it.artificialTransientId.toString() },
                    { it.userName },
                    { it.room.uppercase().replace("SCH ", "").replace("SCH-", "") },
                    { it.intervalMessage },
                    { (_, _, _, _, _, _, _, name, _, _, _, _, _, _, _, extra) -> name + " " + extra.uppercase() },
                    { it.comment },
                    { it.price.toString() }
            )),
    PIZZASCH2(false, "Pizzasch2", OrderStrategy.ORDER_GROUPED.representation,
            listOf("ID", "NÉV", "SZOBA", TIME_WINDOW_HEADER, PRODUCT_HEADER, COMMENT_HEADER, "ÁR"), intArrayOf(3, 8, 4, 5, 10, 10, 4),
            listOf<(OrderEntity) -> String>(
                    { it.artificialTransientId.toString() },
                    { it.userName.replace(Regex(" ?x ?[0-9]+"), "") },
                    { it.room.uppercase().replace("SCH ", "").replace("SCH-", "") },
                    { it.intervalMessage.replace(" ", "") },
                    { (_, _, _, _, _, _, _, name, _, _, _, _, _, _, _, extra) -> name + " " + extra.uppercase() },
                    { it.comment },
                    { (it.price / it.count).toString() }
            ), 16f, true),
    FOODEX(true, "Foodex", OrderStrategy.ORDER_GROUPED.representation,
            listOf("ID", "NÉV", PRODUCT_HEADER, "SZOBA", COMMENT_HEADER, "ÁR"), intArrayOf(3, 8, 5, 4, 10, 4),
            listOf<(OrderEntity) -> String>(
                    { it.artificialTransientId.toString() },
                    { it.userName },
                    { it.name },
                    { it.room },
                    { it.comment },
                    { it.price.toString() }
            ))
    ;

}
