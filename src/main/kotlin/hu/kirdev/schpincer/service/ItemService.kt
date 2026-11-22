package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.config.APP_ZONE
import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.model.ItemEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalTime

/** The "tomorrow" list also covers the night after tomorrow, until this local time. */
private val TOMORROW_CUTOFF: LocalTime = LocalTime.of(6, 0)

@Service
open class ItemService {

    @Autowired
    private lateinit var repo: ItemRepository

    @Autowired
    private lateinit var openingRepo: OpeningRepository

    @Autowired
    private lateinit var extrasService: ExtrasService

    @Transactional(readOnly = true)
    open fun findAll(): List<ItemEntity> {
        return repo.findAllByVisibleTrueOrderByPrecedenceDesc()
    }

    @Transactional(readOnly = true)
    open fun findAll(page: Int): List<ItemEntity> {
        return repo.findAllByVisibleTrueAndVisibleInAllTrueOrderByPrecedenceDesc(PageRequest.of(page, 6)).getContent()
    }

    @Transactional(readOnly = true)
    open fun getOne(itemId: Long): ItemEntity? {
        return repo.getReferenceById(itemId)
    }

    @Transactional(readOnly = false)
    open fun save(itemEntity: ItemEntity) {
        repo.save(itemEntity)
        // Regenerate extras whenever an item gets updated
        extrasService.generateAllExtrasForAllCircles()
    }

    @Transactional(readOnly = true)
    open fun findAllByCircle(circleId: Long): List<ItemEntity> {
        return repo.findAllByCircle_IdOrderByManualPrecedenceDesc(circleId)
    }

    @Transactional(readOnly = false)
    open fun delete(ie: ItemEntity) {
        repo.delete(ie)
    }

    @Transactional(readOnly = false)
    open fun deleteByCircle(circleId: Long) {
        repo.deleteByCircle_Id(circleId)
    }

    @Transactional(readOnly = true)
    open fun findAllByOrderableNow(): List<ItemEntity> =
        findAllOrderableNowAt(Instant.now().toEpochMilli())

    internal fun findAllOrderableNowAt(now: Long): List<ItemEntity> {
        val circleIds = openingRepo.findAllOrderableAt(now)
            .mapNotNull { it.circle?.id }
            .distinct()
        return itemsOfCircles(circleIds)
    }

    @Transactional(readOnly = true)
    open fun findAllByOrerableTomorrow(): List<ItemEntity> =
        findAllOrderableTomorrowAt(Instant.now().toEpochMilli())

    internal fun findAllOrderableTomorrowAt(now: Long): List<ItemEntity> {
        val (from, to) = tomorrowOrderingRange(now)
        val circleIds = openingRepo.findAllOrderableBetween(from, to)
            .mapNotNull { it.circle?.id }
            .distinct()
        return itemsOfCircles(circleIds)
    }

    /** From tomorrow 00:00 until the day after tomorrow 06:00, local time (correct on DST change days too). */
    internal fun tomorrowOrderingRange(nowMillis: Long): Pair<Long, Long> {
        val tomorrow = Instant.ofEpochMilli(nowMillis).atZone(APP_ZONE).toLocalDate().plusDays(1)
        val from = tomorrow.atStartOfDay(APP_ZONE).toInstant().toEpochMilli()
        val to = tomorrow.plusDays(1).atTime(TOMORROW_CUTOFF).atZone(APP_ZONE).toInstant().toEpochMilli()
        return from to to
    }

    private fun itemsOfCircles(circleIds: List<Long>): List<ItemEntity> {
        if (circleIds.isEmpty()) return emptyList()
        return repo.findAllByCircle_IdInOrderByPrecedenceDesc(circleIds)
    }

    @Transactional(readOnly = false)
    open fun saveAll(itemEntities: List<ItemEntity>) {
        repo.saveAll(itemEntities)
        // Regenerate extras whenever an item gets updated
        extrasService.generateAllExtrasForAllCircles()
    }

}
