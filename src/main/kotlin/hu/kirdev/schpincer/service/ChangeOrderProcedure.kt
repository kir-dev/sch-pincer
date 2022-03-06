package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.OrderRepository
import hu.kirdev.schpincer.model.*
import hu.kirdev.schpincer.web.removeNonPrintable

class ChangeOrderProcedure(
        private val user: UserEntity,
        private val id: Long,
        private val orderRepository: OrderRepository,
        private val room: String,
        private val comment: String
) {

    lateinit var order: OrderEntity

    fun changeOrder() {
        loadOrder()
        validatePrivilege()
        validateStatus()
        loadOpening(System.currentTimeMillis())

        updateDetails()
    }

    private fun loadOrder() {
        order = orderRepository.getOne(id)
    }

    private fun validatePrivilege() {
        if (order.userId != user.uid)
            throw FailedOrderException(RESPONSE_BAD_REQUEST)
    }

    private fun validateStatus() {
        if (order.status !== OrderStatus.ACCEPTED)
            throw FailedOrderException(RESPONSE_INVALID_STATUS)
    }

    private fun loadOpening(now: Long) {
        if (order.cancelUntil <= now)
            throw FailedOrderException(RESPONSE_ORDER_PERIOD_ENDED)
    }

    private fun updateDetails() {
        order.room = room
        order.comment = "[${user.cardType.name}] ${comment.removeNonPrintable()}"
    }

}
