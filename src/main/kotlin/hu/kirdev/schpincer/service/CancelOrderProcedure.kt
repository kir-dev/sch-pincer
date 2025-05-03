package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.OrderRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.model.*

class CancelOrderProcedure(
        private val user: UserEntity,
        private val id: Long,
        private val orderRepository: OrderRepository,
        private val openings: OpeningService,
        private val timeWindowRepo: TimeWindowRepository
) {

    internal var count: Int = 0

    lateinit var order: OrderEntity
    lateinit var opening: OpeningEntity
    lateinit var timeWindow: TimeWindowEntity

    fun cancelOrder() {
        loadOrder()
        validatePrivilege()
        validateStatus()
        loadOpening(System.currentTimeMillis())

        updateDetails()
        updateRemainingCounts()
        updateExtraCategories()
    }

    internal fun loadOrder() {
        order = orderRepository.getReferenceById(id)
    }

    internal fun validatePrivilege() {
        if (order.userId != user.uid)
            throw FailedOrderException(RESPONSE_BAD_REQUEST)
    }

    internal fun validateStatus() {
        if (order.status !== OrderStatus.ACCEPTED)
            throw FailedOrderException(RESPONSE_INVALID_STATUS)
    }

    internal fun loadOpening(now: Long) {
        opening = openings.getOne(order.openingId!!)
        if (opening.orderEnd <= now)
            throw FailedOrderException(RESPONSE_ORDER_PERIOD_ENDED)
    }

    internal fun updateDetails() {
        order.status = OrderStatus.CANCELLED
        count = order.count
        timeWindow = timeWindowRepo.getReferenceById(order.intervalId)
    }

    internal fun updateRemainingCounts() {
        timeWindow.normalItemCount = timeWindow.normalItemCount + count
        if (order.extraTag)
            timeWindow.extraItemCount = timeWindow.extraItemCount + count
        opening.orderCount -= count
    }

    internal fun updateExtraCategories() {
        when (ItemCategory.of(order.category)) {
            ItemCategory.ALPHA -> opening.usedAlpha -= count
            ItemCategory.BETA -> opening.usedBeta -= count
            ItemCategory.GAMMA -> opening.usedGamma -= count
            ItemCategory.DELTA -> opening.usedDelta -= count
            ItemCategory.LAMBDA -> opening.usedLambda -= count
            else -> {
                // No extra action is required
            }
        }
    }

}
