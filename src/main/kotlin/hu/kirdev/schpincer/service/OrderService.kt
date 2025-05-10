package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.*
import hu.kirdev.schpincer.dto.ManualUserDetails
import hu.kirdev.schpincer.dto.PriceBreakdown
import hu.kirdev.schpincer.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val List<OrderEntity>.highestPriority: Int
    get() = this.maxByOrNull { it.priority }?.priority ?: 1

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
const val RESPONSE_MANUAL_FAIL = "MANUAL_FAIL"
const val RESPONSE_BAD_REQUEST = "BAD_REQUEST"
const val RESPONSE_INVALID_STATUS = "INVALID_STATUS"
const val RESPONSE_ORDER_PERIOD_ENDED = "ORDER_PERIOD_ENDED"
const val RESPONSE_TIME_WINDOW_INVALID = "TIME_WINDOW_INVALID"
const val BASE_PRICE = "basePrice"

@Service
open class OrderService {

    @Autowired
    internal lateinit var repo: OrderRepository

    @Autowired
    internal lateinit var openingRepo: OpeningRepository

    @Autowired
    internal lateinit var openings: OpeningService

    @Autowired
    internal lateinit var timeWindowRepo: TimeWindowRepository

    @Autowired
    internal lateinit var itemsRepo: ItemRepository

    @Autowired
    internal lateinit var extrasRepository: ExtrasRepository

    @Autowired
    lateinit var messageSource: MessageSource

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
        return repo.getReferenceById(id)
    }

    @Transactional(readOnly = true)
    open fun findAllByOpening(openingId: Long): List<OrderEntity> {
        return repo.findAllByOpeningId(openingId)
    }

    @Transactional
    open fun updateOrder(id: Long, os: OrderStatus) {
        repo.findById(id).ifPresent { orderEntity ->
            orderEntity.status = os
            repo.save(orderEntity)
        }
    }

    @Transactional
    open fun updateOrderComment(id: Long, comment: String) {
        repo.findById(id).ifPresent { orderEntity ->
            orderEntity.comment = orderEntity.comment.substring(0, 5) + comment
            repo.save(orderEntity)
        }
    }

    @Transactional
    open fun updateOrderPrice(id: Long, price: Int) {
        repo.findById(id).ifPresent { orderEntity ->
            orderEntity.price = price
            repo.save(orderEntity)
        }
    }

    @Transactional(readOnly = true)
    open fun getCircleIdByOrderId(id: Long): Long? {
        val order: Optional<OrderEntity> = repo.findById(id)
        return order.map { orderEntity: OrderEntity -> openingRepo.getReferenceById(orderEntity.openingId!!).circle?.id!! }.orElse(null)
    }

    @Transactional(readOnly = false)
    open fun reviewOrder(id: Long, reviewId: Long) {
        val order = getOne(id)!!
        order.reviewId = reviewId
        save(order)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    open fun makeOrder(user: UserEntity, id: Long, itemCount: Int, time: Long, comment: String, detailsJson: String): ResponseEntity<String> {
        val procedure = MakeOrderProcedure(
            user, id, itemCount, time, comment, detailsJson,
            itemsRepo = itemsRepo,
            timeWindowRepo = timeWindowRepo,
            extrasRepository = extrasRepository
        )
        procedure.makeOrder()

        timeWindowRepo.save(procedure.timeWindow)
        openings.save(procedure.current)
        this.save(procedure.order)
        return responseOf(RESPONSE_ACK)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    open fun makeManualOrder(user: UserEntity, id: Long, itemCount: Int, time: Long,
                             comment: String, detailsJson: String, manualUser: ManualUserDetails
    ): ResponseEntity<String> {
        val circleId = itemsRepo.findByIdOrNull(id)?.circle?.id
        if (!((user.permissions.contains("CIRCLE_$circleId")) || user.sysadmin)) {
            return responseOf(RESPONSE_MANUAL_FAIL)
        }

        val procedure = MakeOrderProcedure(
            user, id, itemCount, time, comment, detailsJson,
            itemsRepo = itemsRepo,
            timeWindowRepo = timeWindowRepo,
            extrasRepository = extrasRepository
        )
        procedure.makeOrder(manualUser)

        timeWindowRepo.save(procedure.timeWindow)
        openings.save(procedure.current)
        this.save(procedure.order)
        return responseOf(RESPONSE_ACK)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    open fun cancelOrder(user: UserEntity, id: Long): ResponseEntity<String> {
        val procedure = CancelOrderProcedure(user, id,
                orderRepository = repo,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.cancelOrder()

        timeWindowRepo.save(procedure.timeWindow)
        openings.save(procedure.opening)
        this.save(procedure.order)
        return responseOf(RESPONSE_ACK)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    open fun changeOrder(user: UserEntity, id: Long, room: String, comment: String): ResponseEntity<String> {
        val procedure = ChangeOrderProcedure(user, id,
            orderRepository = repo,
            room = room,
            comment = comment)
        procedure.changeOrder()

        this.save(procedure.order)
        return responseOf(RESPONSE_ACK)
    }

    @Transactional(readOnly = true)
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
                    .map { intervals ->
                        intervals.first to intervals.second
                                .toList()
                                .sortedBy { it.second.highestPriority }
                                .flatMap { it.second }
                    }
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

    @Transactional(readOnly = false)
    open fun closeAllOrdersInOpening(openingId: Long) {
        val orderEntities = repo.findAllByOpeningId(openingId)
                .filter { it.status == OrderStatus.ACCEPTED
                        || it.status == OrderStatus.INTERPRETED
                        || it.status == OrderStatus.COMPLETED
                        || it.status == OrderStatus.HANDED_OVER }
        orderEntities.forEach { it.status = OrderStatus.SHIPPED }
        repo.saveAll(orderEntities)
    }

    @Transactional(readOnly = false)
    open fun cancelAllOrdersInOpening(openingId: Long) {
        val orderEntities = repo.findAllByOpeningId(openingId)
                .filter { it.status == OrderStatus.ACCEPTED }
        orderEntities.forEach { it.status = OrderStatus.CANCELLED }
        repo.saveAll(orderEntities)
    }

    @Transactional(readOnly = false)
    open fun updateStatus(orderId: Long, status: String) {
        repo.findById(orderId).orElse(null)?.let {
            it.status = OrderStatus[status]
            repo.save(it)
        }
    }

    @Transactional(readOnly = false)
    open fun updateChefComment(orderId: Long, comment: String) {
        repo.findById(orderId).orElse(null)?.let {
            it.chefComment = comment
            repo.save(it)
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    open fun changeCancelUntilDates(openingId: Long, orderEnd: Long) {
        val affectedOpenings = repo.findAllByOpeningId(openingId)
        affectedOpenings.forEach { it.cancelUntil = orderEnd }
        repo.saveAll(affectedOpenings)
    }


    @Transactional(readOnly = true)
    open fun generatePriceBreakdowns(orders: List<OrderEntity>): List<PriceBreakdown> {

        return orders.map {
            val prices = mutableMapOf<String, Int>()
            prices[BASE_PRICE] = it.price / it.count.coerceAtLeast(1)
            it.orderedItem?.apply {
                for (extra in it.extras.sortedBy { extra -> extra.name }) {
                    prices["${extra.name}- ${extra.displayName}"] = extra.price
                }
                prices[BASE_PRICE] = if (this.discountPrice == 0) this.price else this.discountPrice
            }

            PriceBreakdown(it.id, prices)
        }

    }
}

fun responseOf(body: String, status: HttpStatus = HttpStatus.OK) = ResponseEntity(body, status)
