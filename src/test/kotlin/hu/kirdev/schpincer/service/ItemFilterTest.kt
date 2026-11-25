package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.config.APP_ZONE
import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OpeningEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
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
    fun `now lists the items of distinct circles that are orderable now`() {
        val now = millis("2026-03-10T15:00")
        whenever(openingRepo.findAllOrderableAt(now)).thenReturn(listOf(
            opening(circleId = 5),
            opening(circleId = 11),
            opening(circleId = null),
            opening(circleId = 5)
        ))
        val expected = listOf(ItemEntity(id = 50, name = "open"))
        whenever(repo.findAllByCircle_IdInOrderByPrecedenceDesc(listOf(5L, 11L))).thenReturn(expected)

        assertEquals(expected, service.findAllOrderableNowAt(now))
    }

    @Test
    fun `now with no orderable openings does not query items`() {
        val now = millis("2026-03-10T15:00")
        whenever(openingRepo.findAllOrderableAt(now)).thenReturn(emptyList())

        assertEquals(emptyList(), service.findAllOrderableNowAt(now))
        verify(repo, never()).findAllByCircle_IdInOrderByPrecedenceDesc(any())
    }

    @Test
    fun `tomorrow looks up openings orderable in tomorrow's range`() {
        val now = millis("2026-03-10T15:00")
        val from = millis("2026-03-11T00:00")
        val to = millis("2026-03-12T06:00")
        whenever(openingRepo.findAllOrderableBetween(from, to)).thenReturn(listOf(
            opening(circleId = 8),
            opening(circleId = 9),
            opening(circleId = 8)
        ))
        val expected = listOf(ItemEntity(id = 80, name = "tomorrow"))
        whenever(repo.findAllByCircle_IdInOrderByPrecedenceDesc(listOf(8L, 9L))).thenReturn(expected)

        assertEquals(expected, service.findAllOrderableTomorrowAt(now))
    }

    @Test
    fun `tomorrow with no orderable openings does not query items`() {
        val now = millis("2026-03-10T15:00")
        val (from, to) = service.tomorrowOrderingRange(now)
        whenever(openingRepo.findAllOrderableBetween(from, to)).thenReturn(emptyList())

        assertEquals(emptyList(), service.findAllOrderableTomorrowAt(now))
        verify(repo, never()).findAllByCircle_IdInOrderByPrecedenceDesc(any())
    }

    @ParameterizedTest(name = "now = {0}")
    @CsvSource(
        "2026-03-10T15:00, 30", // ordinary day
        "2026-03-10T23:59, 30", // just before midnight
        "2026-03-11T00:00, 30", // exactly midnight
        "2026-03-27T12:00, 29", // the day after tomorrow is the spring DST day
        "2026-03-28T12:00, 29", // tomorrow is the spring DST day
        "2026-10-23T12:00, 31", // the day after tomorrow is the autumn DST day
        "2026-10-24T12:00, 31", // tomorrow is the autumn DST day
    )
    fun `tomorrow range is tomorrow 00 00 until the day after 06 00 local time`(now: String, expectedHours: Long) {
        val today = LocalDateTime.parse(now).toLocalDate()

        val (from, to) = service.tomorrowOrderingRange(millis(now))

        assertEquals(today.plusDays(1).atStartOfDay(), local(from))
        assertEquals(today.plusDays(2).atTime(LocalTime.of(6, 0)), local(to))
        assertEquals(expectedHours, Duration.ofMillis(to - from).toHours())
    }

    private fun millis(localDateTime: String): Long =
        LocalDateTime.parse(localDateTime).atZone(APP_ZONE).toInstant().toEpochMilli()

    private fun local(millis: Long): LocalDateTime =
        Instant.ofEpochMilli(millis).atZone(APP_ZONE).toLocalDateTime()

    private fun opening(circleId: Long?) = OpeningEntity(
        dateStart = 0,
        dateEnd = 0,
        orderStart = 0,
        orderEnd = 0,
        circle = circleId?.let { CircleEntity(id = it) }
    )
}
