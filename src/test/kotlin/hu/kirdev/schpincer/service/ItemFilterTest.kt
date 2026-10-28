package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OpeningEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ItemFilterTest {

    @Mock
    lateinit var repo: ItemRepository

    @Mock
    lateinit var openingRepo: OpeningRepository

    @Mock
    lateinit var extrasService: ExtrasService

    @InjectMocks
    lateinit var service: ItemService

    @Test
    fun `now passes only circles whose ordering window contains now`() {
        val now = 1_700_000_000_000L
        whenever(openingRepo.findAllOpenForOrdering(now)).thenReturn(listOf(
            opening(circleId = 5, orderStart = now - 1_000, orderEnd = now + 1_000),
            opening(circleId = 11, orderStart = now, orderEnd = now),
            opening(circleId = 6, orderStart = now - 10_000, orderEnd = now - 1),
            opening(circleId = 7, orderStart = now + 1, orderEnd = now + 10_000),
            opening(circleId = null, orderStart = now - 1, orderEnd = now + 1),
            opening(circleId = 5, orderStart = now - 50, orderEnd = now + 50)
        ))
        val expected = listOf(ItemEntity(id = 50, name = "open"))
        whenever(repo.findAllByCircle_IdInOrderByPrecedenceDesc(listOf(5L, 11L))).thenReturn(expected)

        val result = service.findAllOrderableNowAt(now)

        assertEquals(expected, result)
        verify(repo).findAllByCircle_IdInOrderByPrecedenceDesc(listOf(5L, 11L))
    }

    @Test
    fun `now with no open circles does not query items`() {
        val now = 1_700_000_000_000L
        whenever(openingRepo.findAllOpenForOrdering(now)).thenReturn(emptyList())

        assertEquals(emptyList(), service.findAllOrderableNowAt(now))
        verify(repo, never()).findAllByCircle_IdInOrderByPrecedenceDesc(any())
    }

    @Test
    fun `tomorrow keeps an opening that starts tomorrow afternoon and excludes it from now`() {
        val zone = ZoneId.of("Europe/Budapest")
        val now = ZonedDateTime.of(2026, 3, 10, 15, 0, 0, 0, zone).toInstant().toEpochMilli()
        val from = ZonedDateTime.of(2026, 3, 11, 0, 0, 0, 0, zone).toInstant().toEpochMilli()
        val to = from + DAY_IN_MILLIS + HOUR6_IN_MILLIS
        assertEquals(from to to, service.tomorrowOrderStartRange(now))

        val tomorrowAfternoon = from + 15 * 60 * 60 * 1000
        whenever(openingRepo.findAllByOrderStartGreaterThanAndOrderStartLessThan(from, to)).thenReturn(listOf(
            opening(circleId = 8, orderStart = tomorrowAfternoon, orderEnd = tomorrowAfternoon + 3_600_000),
            opening(circleId = 9, orderStart = now, orderEnd = now + 3_600_000),
            opening(circleId = 10, orderStart = from, orderEnd = from + 3_600_000),
            opening(circleId = 12, orderStart = to, orderEnd = to + 3_600_000)
        ))
        val expected = listOf(ItemEntity(id = 80, name = "tomorrow"))
        whenever(repo.findAllByCircle_IdInOrderByPrecedenceDesc(listOf(8L))).thenReturn(expected)

        assertEquals(expected, service.findAllOrderableTomorrowAt(now))
        verify(openingRepo).findAllByOrderStartGreaterThanAndOrderStartLessThan(from, to)
        verify(repo).findAllByCircle_IdInOrderByPrecedenceDesc(listOf(8L))
    }

    @Test
    fun `an opening that starts tomorrow is excluded from now`() {
        val zone = ZoneId.of("Europe/Budapest")
        val now = ZonedDateTime.of(2026, 3, 10, 15, 0, 0, 0, zone).toInstant().toEpochMilli()
        val tomorrowAfternoon = ZonedDateTime.of(2026, 3, 11, 15, 0, 0, 0, zone).toInstant().toEpochMilli()
        whenever(openingRepo.findAllOpenForOrdering(now)).thenReturn(listOf(
            opening(circleId = 8, orderStart = tomorrowAfternoon, orderEnd = tomorrowAfternoon + 3_600_000)
        ))

        assertEquals(emptyList(), service.findAllOrderableNowAt(now))
        verify(repo, never()).findAllByCircle_IdInOrderByPrecedenceDesc(any())
    }

    @Test
    fun `tomorrow with no matching openings does not query items`() {
        val zone = ZoneId.of("Europe/Budapest")
        val now = ZonedDateTime.of(2026, 3, 10, 15, 0, 0, 0, zone).toInstant().toEpochMilli()
        val (from, to) = service.tomorrowOrderStartRange(now)
        whenever(openingRepo.findAllByOrderStartGreaterThanAndOrderStartLessThan(from, to)).thenReturn(emptyList())

        assertEquals(emptyList(), service.findAllOrderableTomorrowAt(now))
        verify(repo, never()).findAllByCircle_IdInOrderByPrecedenceDesc(any())
    }

    private fun opening(circleId: Long?, orderStart: Long, orderEnd: Long) = OpeningEntity(
        dateStart = orderStart,
        dateEnd = orderEnd,
        orderStart = orderStart,
        orderEnd = orderEnd,
        circle = circleId?.let { CircleEntity(id = it) }
    )
}
