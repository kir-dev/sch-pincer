package hu.kirdev.schpincer.dto

import hu.kirdev.schpincer.model.OrderEntity
import hu.kirdev.schpincer.model.OrderStatus

data class KitchenOrderDto(
        var id: Long = 0,
        var userName: String = "",
        var status: OrderStatus = OrderStatus.ACCEPTED,
        var artificialId: String = "",
        var name: String = "",
        var intervalMessage: String = "",
        var intervalStart: Long = 0,
        var intervalEnds: Long = 0,
        var intervalStatus: String = "",
        var comment: String = "",
        var additionalComment: String = "",
        var chefComment: String = "",
        var room: String = "",
        var price: Int = 0,
        var extra: String = "",
        var extraTag: Boolean = false,
        var count: Int = 1,
        var priority: Int = 1,
) {

    constructor(order: OrderEntity, intervalLength: Int) : this(
            id = order.id,
            userName = order.userName,
            status = order.status,
            artificialId = order.artificialTransientId.toString(),
            name = order.name,
            intervalMessage = order.intervalMessage,
            intervalStart = order.date,
            intervalEnds = order.date + intervalLength,
            comment = order.comment,
            additionalComment = order.additionalComment,
            chefComment = order.chefComment,
            room = order.room,
            price = order.price,
            extra = order.extra,
            extraTag = order.extraTag,
            count = order.count,
            priority = order.priority,
            intervalStatus = when {
                System.currentTimeMillis() < (order.date) -> "before"
                System.currentTimeMillis() > (order.date + intervalLength) -> "after"
                else -> "ok"
            }
    )
}