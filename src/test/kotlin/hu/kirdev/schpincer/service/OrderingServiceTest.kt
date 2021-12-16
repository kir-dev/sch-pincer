package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.OrderRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
class OrderingServiceTest {

    @Mock
    lateinit var itemsRepo: ItemRepository

    @Mock
    lateinit var openings: OpeningService

    @Mock
    lateinit var timeWindowRepo: TimeWindowRepository

    @Mock
    lateinit var orderRepository: OrderRepository

    @Mock
    lateinit var user: UserEntity

    @Test
    fun makeValidOrder() {
        val service = spy(OrderService())

        val opening = OpeningEntity(30, maxOrder = 5, dateStart = 0, dateEnd = 0, orderStart = 0,
                orderEnd = System.currentTimeMillis() * 2, maxBeta = 10)

        val timeWindow = TimeWindowEntity(opening = opening, name = "6:00-8:00", date = 12, normalItemCount = 5, extraItemCount = 4)
        whenever(timeWindowRepo.getOne(40)).thenReturn(timeWindow)
        whenever(timeWindowRepo.findById(40)).thenReturn(Optional.of(timeWindow))
        val item = ItemEntity(name = "name", category = 2, orderable = true, personallyOrderable = false,
                alias = "", circle = CircleEntity(10), price = 1200)
        whenever(itemsRepo.getOne(12)).thenReturn(item)

        doNothing().whenever(service).save(anyOrNull())

        whenever(user.uid).thenReturn("unique")
        whenever(user.room).thenReturn("SCH-1620")
        whenever(user.name).thenReturn("Test User")
        whenever(user.cardType).thenReturn(CardType.KB)
        whenever(user.orderingPriority).thenReturn(5)

        service.itemsRepo = itemsRepo
        service.timeWindowRepo = timeWindowRepo
        service.openings = openings

        service.makeOrder(user, 12, 2, 40, "comment", "{\"answers\": []}")

        verify(service, times(1)).save(anyOrNull())
        verify(timeWindowRepo, times(1)).save(anyOrNull())
        verify(openings, times(1)).save(anyOrNull())
    }

    @Test
    fun cancelValidOrder() {
        val service = spy(OrderService())

        val order = OrderEntity(count = 3, extraTag = true, userId = "unique-id", openingId = 30, intervalId = 70,
                userName = "", comment = "", detailsJson = "", room = "")
        whenever(orderRepository.getOne(4)).thenReturn(order)
        val opening = OpeningEntity(30, orderCount = 4, dateStart = 0, dateEnd = 0, orderStart = 0,
                orderEnd = System.currentTimeMillis() * 2)
        whenever(openings.getOne(30)).thenReturn(opening)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 5, extraItemCount = 3)
        whenever(timeWindowRepo.getOne(70)).thenReturn(timeWindow)
        whenever(user.uid).thenReturn("unique-id")

        service.repo = orderRepository
        service.openings = openings
        service.timeWindowRepo = timeWindowRepo

        service.cancelOrder(user, 4)

        verify(service, times(1)).save(anyOrNull())
        verify(timeWindowRepo, times(1)).save(anyOrNull())
        verify(openings, times(1)).save(anyOrNull())
    }


}