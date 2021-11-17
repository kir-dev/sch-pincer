package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.dto.ManualUserDetails
import hu.kirdev.schpincer.dto.OrderDetailsDto
import hu.kirdev.schpincer.model.*
import hu.kirdev.schpincer.web.component.calculateExtra

class FailedOrderException(val response: String) : RuntimeException()

class MakeOrderProcedure (
        private val user: UserEntity,
        private val id: Long,
        private val itemCount: Int,
        private val time: Long,
        private val comment: String,
        private val detailsJson: String,
        private val itemsRepo: ItemRepository,
        private val openings: OpeningService,
        private val timeWindowRepo: TimeWindowRepository
) {

    internal lateinit var item: ItemEntity
    internal lateinit var details: OrderDetailsDto
    internal var count: Int = 0

    lateinit var current: OpeningEntity
    lateinit var order: OrderEntity
    lateinit var timeWindow: TimeWindowEntity

    /**
     * This function is not reusable.
     * Create new MakeOrderProcedure for every time you need it.
     */
    fun makeOrder(manualUser: ManualUserDetails? = null) {
        if (manualUser == null) {
            validateUserHasRoom()
            createOrderEntity()
            loadTargetItem()
        } else {
            order = OrderEntity(
                    userId = manualUser.id,
                    userName = manualUser.name,
                    comment = "[${manualUser.card}] @ ${user.name} | $comment",
                    detailsJson = detailsJson,
                    room = manualUser.room)
            item = itemsRepo.getOne(id)
        }

        details = calculateExtra(detailsJson, order, item, manualUser?.card ?: user.cardType)
        updateBasicDetails()
        current = openings.findNextOf(item.circle?.id!!) ?: throw FailedOrderException(RESPONSE_INTERNAL_ERROR)
        if (manualUser == null)
            validateOrderable(System.currentTimeMillis())

        clampItemCount()

        if (manualUser == null) {
            validateOrderCount()
            timeWindow = timeWindowRepo.getOne(time)
            validateTimeWindow()
            updateCategoryLimitations()
        } else {
            timeWindow = timeWindowRepo.getOne(time)
        }

        updateRemainingItemCount()
        updateOrderDetails()
    }

    internal fun validateUserHasRoom() {
        if (user.room.isEmpty())
            throw FailedOrderException(RESPONSE_NO_ROOM_SET)
    }

    internal fun createOrderEntity() {
        order = OrderEntity(
                userId = user.uid,
                userName = user.name,
                comment = "[${user.cardType.name}] $comment",
                detailsJson = detailsJson,
                room = user.room)
    }

    internal fun loadTargetItem() {
        item = itemsRepo.getOne(id)
        if (!item.orderable || item.personallyOrderable)
            throw FailedOrderException(RESPONSE_INTERNAL_ERROR)
    }

    internal fun updateBasicDetails() {
        order.intervalId = time
        order.name = item.name
        order.openingId = openings.findNextOf(item.circle?.id!!)?.id!!
    }

    internal fun validateOrderable(now: Long) {
        if (current.orderStart > now || current.orderEnd < now)
            throw FailedOrderException(RESPONSE_NO_ORDERING)
    }

    internal fun validateOrderCount() {
        if (current.orderCount + count > current.maxOrder)
            throw FailedOrderException(RESPONSE_OVERALL_MAX_REACHED)
    }

    internal fun validateTimeWindow() {
        if (!timeWindow.opening?.id!!.equals(current.id))
            throw FailedOrderException(RESPONSE_INTERNAL_ERROR)
        if (timeWindow.normalItemCount - count < 0)
            throw FailedOrderException(RESPONSE_MAX_REACHED)
        if (order.extraTag && timeWindow.extraItemCount - count < 0)
            throw FailedOrderException(RESPONSE_MAX_REACHED_EXTRA)
    }

    internal fun updateCategoryLimitations() {
        when (ItemCategory.of(item.category)) {
            ItemCategory.ALPHA ->
                if (current.usedAlpha + count <= current.maxAlpha)
                    current.usedAlpha += count
                else throw FailedOrderException(RESPONSE_CATEGORY_FULL)

            ItemCategory.BETA ->
                if (current.usedBeta + count <= current.maxBeta)
                    current.usedBeta += count
                else throw FailedOrderException(RESPONSE_CATEGORY_FULL)

            ItemCategory.GAMMA ->
                if (current.usedGamma + count <= current.maxGamma)
                    current.usedGamma += count
                else throw FailedOrderException(RESPONSE_CATEGORY_FULL)

            ItemCategory.DELTA ->
                if (current.usedDelta + count <= current.maxDelta)
                    current.usedDelta += count
                else throw FailedOrderException(RESPONSE_CATEGORY_FULL)

            ItemCategory.LAMBDA ->
                if (current.usedLambda + count <= current.maxLambda)
                    current.usedLambda += count
                else throw FailedOrderException(RESPONSE_CATEGORY_FULL)

            else -> {
                // No extra action is required
            }
        }
    }

    internal fun clampItemCount() {
        count = Math.max(1, if (itemCount < details.minCount) {
            details.minCount
        } else if (itemCount > details.maxCount) {
            details.maxCount
        } else {
            itemCount
        })
    }

    internal fun updateRemainingItemCount() {
        timeWindow.normalItemCount = timeWindow.normalItemCount - count
        if (order.extraTag)
            timeWindow.extraItemCount = timeWindow.extraItemCount - count
        current.orderCount = current.orderCount + count
    }

    internal fun updateOrderDetails() {
        with(order) {
            date = timeWindow.date
            price = order.price * this@MakeOrderProcedure.count
            intervalMessage = timeWindow.name
            cancelUntil = current.orderEnd
            category = item.category
            priority = user.orderingPriority
            compactName = (if (item.alias.isEmpty()) item.name else item.alias) + (if (this@MakeOrderProcedure.count == 1) "" else " x ${this@MakeOrderProcedure.count}")
            order.count = this@MakeOrderProcedure.count
        }
    }
}
