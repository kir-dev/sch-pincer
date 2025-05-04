package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.model.ItemEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

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
    open fun findAllByOrderableNow(): List<ItemEntity> {
        val time = Instant.now().toEpochMilli()
        val circles = openingRepo.findAllByOrderStartLessThanAndOrderEndGreaterThan(time, time)
                .map { opening -> opening.circle?.id!! }
        return repo.findAllByCircle_IdInOrderByPrecedenceDesc(circles)
    }

    @Transactional(readOnly = true)
    open fun findAllByOrerableTomorrow(): List<ItemEntity> {
        val time1 = getJustDateFrom(Instant.now().toEpochMilli()) + DAY_IN_MILLIS
        val time2 = time1 + DAY_IN_MILLIS + HOUR6_IN_MILLIS
        val circles = openingRepo.findAllByOrderStartGreaterThanAndOrderStartLessThan(time1, time2)
                .map { opening -> opening.circle?.id!! }
        return repo.findAllByCircle_IdInOrderByPrecedenceDesc(circles)
    }

    @Transactional(readOnly = false)
    open fun getJustDateFrom(millis: Long): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = millis
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0
        return c.timeInMillis
    }

    @Transactional(readOnly = false)
    open fun saveAll(itemEntities: List<ItemEntity>) {
        repo.saveAll(itemEntities)
        // Regenerate extras whenever an item gets updated
        extrasService.generateAllExtrasForAllCircles()
    }

}
