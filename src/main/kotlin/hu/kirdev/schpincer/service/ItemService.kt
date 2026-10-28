package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.model.ItemEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneId

const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000
const val HOUR6_IN_MILLIS = 6 * 60 * 60 * 1000

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
        val circleIds = openingRepo.findAllOpenForOrdering(now)
            .filter { it.orderStart <= now && it.orderEnd >= now }
            .mapNotNull { it.circle?.id }
            .distinct()
        return itemsOfCircles(circleIds)
    }

    @Transactional(readOnly = true)
    open fun findAllByOrerableTomorrow(): List<ItemEntity> =
        findAllOrderableTomorrowAt(Instant.now().toEpochMilli())

    internal fun findAllOrderableTomorrowAt(now: Long): List<ItemEntity> {
        val (from, to) = tomorrowOrderStartRange(now)
        val circleIds = openingRepo.findAllByOrderStartGreaterThanAndOrderStartLessThan(from, to)
            .filter { it.orderStart > from && it.orderStart < to }
            .mapNotNull { it.circle?.id }
            .distinct()
        return itemsOfCircles(circleIds)
    }

    internal fun tomorrowOrderStartRange(nowMillis: Long): Pair<Long, Long> {
        val zone = ZoneId.of("Europe/Budapest")
        val from = Instant.ofEpochMilli(nowMillis)
            .atZone(zone)
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
        return from to from + DAY_IN_MILLIS + HOUR6_IN_MILLIS
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
