package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.model.OpeningEntity
import hu.kirdev.schpincer.model.TimeWindowEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@Service
@Transactional
open class OpeningService {

    companion object {
        val DATE_FORMATTER_HH_MM by lazy { SimpleDateFormat("HH:mm") }
        private const val WEEK = 1000L * 60L * 60L * 24L * 7L
    }

    @Autowired
    private lateinit var repo: OpeningRepository

    @Autowired
    private lateinit var twRepo: TimeWindowRepository

    @Transactional(readOnly = true)
    open fun findAll(): List<OpeningEntity> {
        return repo.findAllByOrderByDateStart()
    }

    @Transactional(readOnly = true)
    open fun findUpcomingOpenings(): List<OpeningEntity> {
        return repo.findAllByOrderEndGreaterThanOrderByDateStart(Instant.now().toEpochMilli())
    }

    @Transactional(readOnly = true)
    open fun findNextWeek(): List<OpeningEntity> {
        return repo.findAllByDateEndGreaterThanAndDateEndLessThanOrderByDateStart(
                Instant.now().toEpochMilli(),
                Instant.now().toEpochMilli() + WEEK)
    }

    @Transactional(readOnly = true)
    open fun findEndedBefore(before: Long, limit: Long): List<OpeningEntity> {
        return repo.findAllEndedOpeningsBefore(Instant.now().toEpochMilli(), before, limit)
    }

    @Transactional(readOnly = false)
    open fun save(opening: OpeningEntity) {
        repo.save(opening)
    }

    @Transactional(readOnly = true)
    open fun findNextOf(id: Long): OpeningEntity? {
        return repo.findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(id, Instant.now().toEpochMilli()).orElse(null)
    }

    @Transactional(readOnly = true)
    open fun findNextStartDateOf(id: Long): Long? {
        val opening: Optional<OpeningEntity> = repo.findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(id, Instant.now().toEpochMilli())
        return opening.map(OpeningEntity::dateStart).orElse(null)
    }

    @Transactional(readOnly = true)
    open fun getOne(openingId: Long): OpeningEntity {
        return repo.getReferenceById(openingId)
    }

    @Transactional(readOnly = false)
    open fun delete(oe: OpeningEntity) {
        repo.delete(oe)
    }

    @Transactional(readOnly = false)
    open fun saveTimeWindow(tw: TimeWindowEntity) {
        twRepo.save(tw)
    }

    @Transactional(readOnly = true)
    open fun isCircleMatches(openingId: Long, circleId: Long): Boolean {
        return repo.findById(openingId).map { (it.circle?.id ?: 0) == circleId }.orElse(false)
    }

}
