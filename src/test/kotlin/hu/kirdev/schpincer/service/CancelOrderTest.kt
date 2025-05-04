package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.OrderRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.getException
import hu.kirdev.schpincer.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class CancelOrderTest {

    @Mock
    lateinit var orderRepository: OrderRepository

    @Mock
    lateinit var openings: OpeningService

    @Mock
    lateinit var timeWindowRepo: TimeWindowRepository

    @Mock
    lateinit var user: UserEntity

    @Test
    fun `validate order loading`() {
        val order = OrderEntity(userId = "", userName = "", comment = "", detailsJson = "", room = "")
        whenever(orderRepository.getReferenceById(4)).thenReturn(order)

        val procedure = CancelOrderProcedure(user, 4,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)

        assertDoesNotThrow { procedure.loadOrder() }
        assertEquals(order, procedure.order)
    }

    @Test
    fun `validate user privilege`() {
        val order = OrderEntity(userId = "unique-id", userName = "", comment = "", detailsJson = "", room = "")
        whenever(user.uid).thenReturn("unique-id")

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.order = order

        assertDoesNotThrow { procedure.validatePrivilege() }
    }

    @Test
    fun `validate invalid user privilege`() {
        val order = OrderEntity(userId = "different", userName = "", comment = "", detailsJson = "", room = "")
        whenever(user.uid).thenReturn("unique-id")

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.order = order

        assertThrows<FailedOrderException> { procedure.validatePrivilege() }
        assertEquals(RESPONSE_BAD_REQUEST, getException<FailedOrderException> { procedure.validatePrivilege() }.response)
    }

    @Test
    fun `validate order status`() {
        val order = OrderEntity(status = OrderStatus.ACCEPTED, userId = "", userName = "", comment = "", detailsJson = "", room = "")

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.order = order

        assertDoesNotThrow { procedure.validateStatus() }
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus::class, names = ["CANCELLED", "INTERPRETED", "SHIPPED", "UNKNOWN"])
    fun `validate invalid order status`(status: OrderStatus) {
        val order = OrderEntity(status = status, userId = "", userName = "", comment = "", detailsJson = "", room = "")

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.order = order

        assertThrows<FailedOrderException> { procedure.validateStatus() }
        assertEquals(RESPONSE_INVALID_STATUS, getException<FailedOrderException> { procedure.validateStatus() }.response)
    }

    @Test
    fun `validate opening loading`() {
        val order = OrderEntity(status = OrderStatus.ACCEPTED, openingId = 30, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 50)
        whenever(openings.getOne(30)).thenReturn(opening)

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.opening = opening
        procedure.order = order

        assertDoesNotThrow { procedure.loadOpening(49) }
    }

    @Test
    fun `validate invalid opening loading`() {
        val order = OrderEntity(status = OrderStatus.ACCEPTED, openingId = 30, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 50)
        whenever(openings.getOne(30)).thenReturn(opening)

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.opening = opening
        procedure.order = order

        assertEquals(RESPONSE_ORDER_PERIOD_ENDED, getException<FailedOrderException> { procedure.loadOpening(50) }.response)
        assertEquals(RESPONSE_ORDER_PERIOD_ENDED, getException<FailedOrderException> { procedure.loadOpening(51) }.response)
    }

    @Test
    fun `validate update details`() {
        val order = OrderEntity(status = OrderStatus.ACCEPTED, intervalId = 70, count = 3,
                userId = "", userName = "", comment = "", detailsJson = "", room = "")
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 5)
        whenever(timeWindowRepo.getReferenceById(70)).thenReturn(timeWindow)

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.order = order

        assertDoesNotThrow { procedure.updateDetails() }
        assertEquals(OrderStatus.CANCELLED, procedure.order.status)
        assertEquals(3, procedure.count)
        assertEquals(timeWindow, procedure.timeWindow)
    }

    @Test
    fun `validate update remaining counts`() {
        val order = OrderEntity(count = 3, extraTag = false,
                userId = "", userName = "", comment = "", detailsJson = "", room = "")
        val opening = OpeningEntity(30, orderCount = 4, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 5, extraItemCount = 3)

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.order = order
        procedure.opening = opening
        procedure.timeWindow = timeWindow
        procedure.count = 3

        assertDoesNotThrow { procedure.updateRemainingCounts() }
        assertEquals(8, procedure.timeWindow.normalItemCount)
        assertEquals(3, procedure.timeWindow.extraItemCount)
        assertEquals(1, procedure.opening.orderCount)
    }

    @Test
    fun `validate update remaining counts with extra`() {
        val order = OrderEntity(count = 3, extraTag = true,
                userId = "", userName = "", comment = "", detailsJson = "", room = "")
        val opening = OpeningEntity(30, orderCount = 4, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 5, extraItemCount = 3)

        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.order = order
        procedure.opening = opening
        procedure.timeWindow = timeWindow
        procedure.count = 3

        assertDoesNotThrow { procedure.updateRemainingCounts() }
        assertEquals(8, procedure.timeWindow.normalItemCount)
        assertEquals(6, procedure.timeWindow.extraItemCount)
        assertEquals(1, procedure.opening.orderCount)
    }

    private val usedPerCategory = arrayOf(
            arrayOf(3, 0, 0, 0, 0),
            arrayOf(0, 3, 0, 0, 0),
            arrayOf(0, 0, 3, 0, 0),
            arrayOf(0, 0, 0, 3, 0),
            arrayOf(0, 0, 0, 0, 3)
    )

    private val availablePerCategory = arrayOf(
            arrayOf(1, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 0),
            arrayOf(0, 0, 1, 0, 0),
            arrayOf(0, 0, 0, 1, 0),
            arrayOf(0, 0, 0, 0, 1)
    )

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4])
    fun `check category limitations`(categoryId: Int) {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        opening.usedAlpha = usedPerCategory[categoryId][0]
        opening.usedBeta = usedPerCategory[categoryId][1]
        opening.usedGamma = usedPerCategory[categoryId][2]
        opening.usedDelta = usedPerCategory[categoryId][3]
        opening.usedLambda = usedPerCategory[categoryId][4]

        val order = OrderEntity(category = categoryId + 1, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        val count = 2
        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.opening = opening
        procedure.order = order
        procedure.count = count

        assertDoesNotThrow { procedure.updateExtraCategories() }
        assertEquals(availablePerCategory[categoryId][0], opening.usedAlpha)
        assertEquals(availablePerCategory[categoryId][1], opening.usedBeta)
        assertEquals(availablePerCategory[categoryId][2], opening.usedGamma)
        assertEquals(availablePerCategory[categoryId][3], opening.usedDelta)
        assertEquals(availablePerCategory[categoryId][4], opening.usedLambda)
    }

    @Test
    fun `check category limitations for default`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        opening.usedAlpha = 1
        opening.usedBeta = 2
        opening.usedGamma = 3
        opening.usedDelta = 4
        opening.usedLambda = 5

        val order = OrderEntity(category = ItemCategory.DEFAULT.id,
                userId = "", userName = "", comment = "", detailsJson = "", room = "")
        val count = 2
        val procedure = CancelOrderProcedure(user, 0,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)
        procedure.opening = opening
        procedure.order = order
        procedure.count = count

        assertDoesNotThrow { procedure.updateExtraCategories() }
        assertEquals(1, opening.usedAlpha)
        assertEquals(2, opening.usedBeta)
        assertEquals(3, opening.usedGamma)
        assertEquals(4, opening.usedDelta)
        assertEquals(5, opening.usedLambda)
    }

    @Test
    fun `composite test`() {
        val order = OrderEntity(count = 3, extraTag = true, userId = "unique-id", openingId = 30, intervalId = 70,
                userName = "", comment = "", detailsJson = "", room = "")
        whenever(orderRepository.getReferenceById(4)).thenReturn(order)
        val opening = OpeningEntity(30, orderCount = 4, dateStart = 0, dateEnd = 0, orderStart = 0,
                orderEnd = Instant.now().toEpochMilli() * 2)
        whenever(openings.getOne(30)).thenReturn(opening)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 5, extraItemCount = 3)
        whenever(timeWindowRepo.getReferenceById(70)).thenReturn(timeWindow)
        whenever(user.uid).thenReturn("unique-id")

        val procedure = CancelOrderProcedure(user, 4,
                orderRepository = orderRepository,
                openings = openings,
                timeWindowRepo = timeWindowRepo)

        assertDoesNotThrow { procedure.cancelOrder() }
        assertEquals(8, procedure.timeWindow.normalItemCount)
        assertEquals(6, procedure.timeWindow.extraItemCount)
        assertEquals(1, procedure.opening.orderCount)
        assertEquals(OrderStatus.CANCELLED, procedure.order.status)
        assertEquals(3, procedure.count)

        assertEquals(timeWindow, procedure.timeWindow)
        assertEquals(opening, procedure.opening)
        assertEquals(order, procedure.order)
    }

}
