package hu.kirdev.schpincer.service

import com.fasterxml.jackson.databind.ObjectMapper
import hu.kirdev.schpincer.dao.ExtrasRepository
import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.dto.ManualUserDetails
import hu.kirdev.schpincer.dto.OrderDetailsDto
import hu.kirdev.schpincer.model.*
import hu.kirdev.schpincer.web.component.*
import hu.kirdev.schpincer.web.removeNonPrintable
import java.time.Instant

class FailedOrderException(val response: String) : RuntimeException()

class MakeOrderProcedure (
        private val user: UserEntity,
        private val id: Long,
        private val itemCount: Int,
        private val time: Long,
        private val comment: String,
        private val detailsJson: String,
        private val itemsRepo: ItemRepository,
        private val timeWindowRepo: TimeWindowRepository,
        private val extrasRepository: ExtrasRepository
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
                    comment = "[${manualUser.card}] @ ${user.name} | ${comment.removeNonPrintable()}",
                    additionalComment = "via ${user.name}",
                    detailsJson = detailsJson,
                    room = manualUser.room,
                    createdAt = Instant.now().toEpochMilli())
            item = itemsRepo.getReferenceById(id)
        }

        details = calculateExtra(detailsJson, order, item, manualUser?.card ?: user.grantedCardType)
        updateBasicDetails()

        order.orderedItem = item
        order.extras = getExtrasOfOrder()
        if (manualUser == null)
            validateOrderable(Instant.now().toEpochMilli())

        clampItemCount()

        if (manualUser == null) {
            validateOrderCount()
            timeWindow = timeWindowRepo.getReferenceById(time)
            validateTimeWindow()
            updateCategoryLimitations(true)
        } else {
            updateCategoryLimitations(false)
            timeWindow = timeWindowRepo.getReferenceById(time)
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
                comment = "[${user.grantedCardType.name}] $comment",
                detailsJson = detailsJson,
                room = user.room,
                createdAt = Instant.now().toEpochMilli())
    }

    internal fun loadTargetItem() {
        item = itemsRepo.getReferenceById(id)
        if (!item.orderable || item.personallyOrderable)
            throw FailedOrderException(RESPONSE_INTERNAL_ERROR)
    }

    internal fun updateBasicDetails() {
        order.intervalId = time
        order.name = item.name
        current = timeWindowRepo.findById(time).map { it.opening }.orElse(null) ?: throw FailedOrderException(RESPONSE_TIME_WINDOW_INVALID)
        order.openingId = current.id
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
        if (timeWindow.opening?.id!! != current.id)
            throw FailedOrderException(RESPONSE_INTERNAL_ERROR)
        if (timeWindow.normalItemCount - count < 0)
            throw FailedOrderException(RESPONSE_MAX_REACHED)
        if (order.extraTag && timeWindow.extraItemCount - count < 0)
            throw FailedOrderException(RESPONSE_MAX_REACHED_EXTRA)
    }

    internal fun updateCategoryLimitations(force: Boolean) {
        when (ItemCategory.of(item.category)) {
            ItemCategory.ALPHA -> {
                if (current.usedAlpha + count <= current.maxAlpha)
                    current.usedAlpha += count
                else if (force) throw FailedOrderException(RESPONSE_CATEGORY_FULL)
                else return
            }

            ItemCategory.BETA -> {
                if (current.usedBeta + count <= current.maxBeta)
                    current.usedBeta += count
                else if (force) throw FailedOrderException(RESPONSE_CATEGORY_FULL)
                else return
            }

            ItemCategory.GAMMA -> {
                if (current.usedGamma + count <= current.maxGamma)
                    current.usedGamma += count
                else if (force) throw FailedOrderException(RESPONSE_CATEGORY_FULL)
                else return
            }

            ItemCategory.DELTA -> {
                if (current.usedDelta + count <= current.maxDelta)
                    current.usedDelta += count
                else if (force)  throw FailedOrderException(RESPONSE_CATEGORY_FULL)
                else return
            }

            ItemCategory.LAMBDA -> {
                if (current.usedLambda + count <= current.maxLambda)
                    current.usedLambda += count
                else if (force) throw FailedOrderException(RESPONSE_CATEGORY_FULL)
                else return
            }

            else -> {
                // No extra action is required
            }
        }
    }

    internal fun clampItemCount() {
        count = 1.coerceAtLeast(
            if (itemCount < details.minCount) {
                details.minCount
            } else if (itemCount > details.maxCount) {
                details.maxCount
            } else {
                itemCount
            }
        )
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
            compactName = (item.alias.ifEmpty { item.name }) + (if (this@MakeOrderProcedure.count == 1) "" else " x ${this@MakeOrderProcedure.count}")
            order.count = this@MakeOrderProcedure.count
        }
    }

    private fun getExtrasOfOrder(): Set<ExtraEntity> {

        val extras = mutableSetOf<ExtraEntity>()
        val mapper = ObjectMapper()
        val answers = mapper.readValue(detailsJson.toByteArray(), CustomComponentAnswerList::class.java)

        for (answer in answers.answers) {
            val type = CustomComponentType.valueOf(answer.type)
            val name = answer.name
            for (selected in answer.selected) {

                val optionalExtra = extrasRepository.findByCircleAndNameAndInputTypeAndSelectedIndex(current.circle!!, name, type, selected)
                if (optionalExtra.isEmpty) {
                    throw IllegalArgumentException("${current.circle?.displayName ?: "null"} has no optional extra with input type $type and name $name")
                }
                val extra = optionalExtra.get()
                extras.add(extra)

            }
        }

        return extras

    }
}
