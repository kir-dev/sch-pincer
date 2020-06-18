package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.dao.OrderRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.dto.OrderDetailsDto
import hu.kirdev.schpincer.model.*
import hu.kirdev.schpincer.web.component.calculateExtra
import hu.kirdev.schpincer.web.getUserId
import hu.kirdev.schpincer.web.getUserIfPresent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletRequest

private val List<OrderEntity>.highestPriority: Int
    get() = this.maxBy { it.priority }?.priority ?: 1

enum class OrderStrategy(val representation: String) {
    ORDER_ABSOLUTE("absolute"),
    ORDER_GROUPED("grouped"),
    ORDER_ROOMS("rooms");
}

const val RESPONSE_NO_ROOM_SET = "NO_ROOM_SET"
const val RESPONSE_INTERNAL_ERROR = "INTERNAL_ERROR"
const val RESPONSE_NO_ORDERING = "NO_ORDERING"
const val RESPONSE_OVERALL_MAX_REACHED = "OVERALL_MAX_REACHED"
const val RESPONSE_MAX_REACHED = "MAX_REACHED"
const val RESPONSE_MAX_REACHED_EXTRA = "MAX_REACHED_EXTRA"
const val RESPONSE_CATEGORY_FULL = "CATEGORY_FULL"
const val RESPONSE_ACK = "ACK"
const val RESPONSE_BAD_REQUEST = "BAD_REQUEST"
const val RESPONSE_INVALID_STATUS = "INVALID_STATUS"
const val RESPONSE_ORDER_PERIOD_ENDED = "ORDER_PERIOD_ENDED"

@Service
open class OrderService {

    @Autowired
    private lateinit var repo: OrderRepository

    @Autowired
    private lateinit var openingRepo: OpeningRepository

    @Autowired
    private lateinit var openings: OpeningService

    @Autowired
    private lateinit var timewindowRepo: TimeWindowRepository

    @Autowired
    private lateinit var itemsRepo: ItemRepository

    @Transactional
    open fun save(order: OrderEntity) {
        repo.save(order)
    }

    @Transactional(readOnly = true)
    open fun findAll(uid: String): List<OrderEntity> {
        return repo.findAllByUserIdOrderByDateDesc(uid)
    }

    @Transactional(readOnly = true)
    open fun getOne(id: Long): OrderEntity? {
        return repo.getOne(id)
    }

    @Transactional(readOnly = true)
    open fun findAllByOpening(openingId: Long): List<OrderEntity> {
        return repo.findAllByOpeningId(openingId)
    }

    @Transactional
    open fun updateOrder(id: Long, os: OrderStatus) {
        val order: Optional<OrderEntity> = repo.findById(id)
        if (order.isPresent()) {
            val orderEntity: OrderEntity = order.get()
            orderEntity.status = os
            repo.save(orderEntity)
        }
    }

    @Transactional(readOnly = true)
    open fun getCircleIdByOrderId(id: Long): Long? {
        val order: Optional<OrderEntity> = repo.findById(id)
        return order.map { orderEntity: OrderEntity -> openingRepo.getOne(orderEntity.openingId!!).circle?.id!! }.orElse(null)
    }

    /**
     * TODO: Refactor: Make it to more smaller functions
     */
    @Transactional
    @Throws(IOException::class)
    open fun makeOrder(request: HttpServletRequest, id: Long, itemCount: Int, time: Long, comment: String, detailsJson: String): ResponseEntity<String> {
        val user = request.getUserIfPresent() ?: return responseOf("Error 403", HttpStatus.FORBIDDEN)
        if (user.room.isEmpty())
            return responseOf(RESPONSE_NO_ROOM_SET)

        val order = OrderEntity(
                userId = user.uid!!,
                userName = user.name,
                comment = "[${user.cardType.name}] $comment",
                detailsJson = detailsJson,
                room = user.room)

        order.intervalId = time
        val item: ItemEntity = itemsRepo.getOne(id)
        if (!item.orderable || item.personallyOrderable)
            return responseOf(RESPONSE_INTERNAL_ERROR)
        order.name = item.name

        val details: OrderDetailsDto = calculateExtra(detailsJson, order, item)
        order.openingId = openings.findNextOf(item.circle?.id!!)?.id!!
        val current = openings.findNextOf(item.circle?.id!!) ?: return responseOf(RESPONSE_INTERNAL_ERROR)

        if (current.orderStart > System.currentTimeMillis() || current.orderEnd < System.currentTimeMillis())
            return responseOf(RESPONSE_NO_ORDERING)

        val count = Math.max(1, if (itemCount < details.minCount) {
            details.minCount
        } else if (itemCount > details.maxCount) {
            details.maxCount
        } else {
            itemCount
        })

        if (current.orderCount + count > current.maxOrder)
            return responseOf(RESPONSE_OVERALL_MAX_REACHED)

        val timewindow: TimeWindowEntity = timewindowRepo.getOne(time)
        if (!timewindow.opening?.id!!.equals(current.id))
            return responseOf(RESPONSE_INTERNAL_ERROR)
        if (timewindow.normalItemCount - count < 0)
            return responseOf(RESPONSE_MAX_REACHED)
        if (order.extraTag && timewindow.extraItemCount - count < 0)
            return responseOf(RESPONSE_MAX_REACHED_EXTRA)

        when (ItemCategory.of(item.category)) {
            ItemCategory.ALPHA ->
                if (current.usedAlpha < current.maxAlpha) current.usedAlpha += count
                else return responseOf(RESPONSE_CATEGORY_FULL)

            ItemCategory.BETA ->
                if (current.usedBeta < current.maxBeta) current.usedBeta += count
                else return responseOf(RESPONSE_CATEGORY_FULL)

            ItemCategory.GAMMA ->
                if (current.usedGamma < current.maxGamma) current.usedGamma += count
                else return responseOf(RESPONSE_CATEGORY_FULL)

            ItemCategory.DELTA ->
                if (current.usedDelta < current.maxDelta) current.usedDelta += count
                else return responseOf(RESPONSE_CATEGORY_FULL)

            ItemCategory.LAMBDA ->
                if (current.usedLambda < current.maxLambda) current.usedLambda += count
                else return responseOf(RESPONSE_CATEGORY_FULL)

            ItemCategory.DEFAULT -> {}
        }

        timewindow.normalItemCount = timewindow.normalItemCount - count
        if (order.extraTag)
            timewindow.extraItemCount = timewindow.extraItemCount - count

        current.orderCount = current.orderCount + count
        with (order) {
            date = timewindow.date
            price = order.price * count
            intervalMessage = timewindow.name
            cancelUntil = current.orderEnd
            category = item.category
            priority = user.orderingPriority
            compactName = (if (item.alias.isEmpty()) item.name else item.alias) + (if (count == 1) "" else " x $count")
            order.count = count
        }
        timewindowRepo.save(timewindow)
        openings.save(current)

        save(order)
        return responseOf(RESPONSE_ACK)
    }

    /**
     * TODO: Why is it deprecated?
     */
    @Deprecated("")
    open fun cancelOrder(request: HttpServletRequest, id: Long): ResponseEntity<String> {
        return try {
            val order = getOne(id)
            if (order!!.userId != request.getUserId())
                return responseOf(RESPONSE_BAD_REQUEST, HttpStatus.BAD_REQUEST)

            if (order.status !== OrderStatus.ACCEPTED)
                return responseOf(RESPONSE_INVALID_STATUS)

            val opening = openings.getOne(order.openingId!!)
            if (opening.orderEnd <= System.currentTimeMillis())
                return responseOf(RESPONSE_ORDER_PERIOD_ENDED)

            order.status = OrderStatus.CANCELLED
            val count = order.count
            val timeWindow = timewindowRepo.getOne(order.intervalId)
            timeWindow.normalItemCount = timeWindow.normalItemCount + count

            if (order.extraTag)
                timeWindow.extraItemCount = timeWindow.extraItemCount + count
            opening.orderCount -= count

            when (ItemCategory.of(order.category)) {
                ItemCategory.ALPHA -> opening.usedAlpha -= count
                ItemCategory.BETA -> opening.usedBeta -= count
                ItemCategory.GAMMA -> opening.usedGamma -= count
                ItemCategory.DELTA -> opening.usedDelta -= count
                ItemCategory.LAMBDA -> opening.usedLambda -= count
                ItemCategory.DEFAULT -> {}
            }

            timewindowRepo.save(timeWindow)
            openings.save(opening)
            save(order)
            responseOf(RESPONSE_ACK)
        } catch (e: Exception) {
            responseOf(RESPONSE_BAD_REQUEST, HttpStatus.BAD_REQUEST)
        }
    }

    open fun findToExport(openingId: Long, orderBy: String): List<OrderEntity> {
        return when (orderBy) {
            OrderStrategy.ORDER_ABSOLUTE.representation -> appendArtificialId(repo.findAllByOpeningIdAndStatusNotOrderByPriorityDescDateAsc(openingId, OrderStatus.CANCELLED))

            OrderStrategy.ORDER_GROUPED.representation -> appendArtificialId(repo.findAllByOpeningIdAndStatusNotOrderByIntervalIdAscPriorityDescDateAsc(openingId, OrderStatus.CANCELLED))

            OrderStrategy.ORDER_ROOMS.representation -> appendArtificialId(repo.findAllByOpeningIdAndStatusNot(openingId, OrderStatus.CANCELLED)
                    .groupBy { it.intervalId }
                    .entries
                    .map { intervals -> Pair(intervals.key, intervals.value.groupBy { it.room }) }
                    .toList()
                    .sortedBy { it.first }
                    .map { intervals -> intervals.first to intervals.second
                            .toList()
                            .sortedBy { it.second.highestPriority }
                            .flatMap { it.second } }
                    .flatMap { it.second }
            )

            else -> appendArtificialId(repo.findAllByOpeningIdAndStatusNotOrderByPriorityDescDateAsc(openingId, OrderStatus.CANCELLED))
        }

    }

    private fun appendArtificialId(source: List<OrderEntity>): List<OrderEntity> {
        var id = 1
        for (order in source)
            order.artificialTransientId = id++
        return source
    }

    private fun responseOf(body: String, status: HttpStatus = HttpStatus.OK) = ResponseEntity(body, status)

}
