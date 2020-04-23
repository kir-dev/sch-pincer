package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.model.OpeningEntity
import hu.kirdev.schpincer.model.TimeWindowEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
open class OpeningService {

    companion object {
        val DATE_FORMATTER_HH_MM = SimpleDateFormat("HH:mm")
        private const val WEEK = 1000L * 60L * 60L * 24L * 7L
    }

    @Autowired
    private lateinit var repo: OpeningRepository

    @Autowired
    private lateinit var twRepo: TimeWindowRepository

    open fun findAll(): List<OpeningEntity> {
        return repo.findAllByOrderByDateStart()
    }

    open fun findUpcomingOpenings(): List<OpeningEntity> {
        return repo.findAllByOrderEndGreaterThanOrderByDateStart(System.currentTimeMillis())
    }

    open fun findNextWeek(): List<OpeningEntity> {
        return repo.findAllByDateEndGreaterThanAndDateEndLessThanOrderByDateStart(
                System.currentTimeMillis(),
                System.currentTimeMillis() + WEEK)
    }

    open fun save(opening: OpeningEntity) {
        repo.save(opening)
    }

    open fun findNextOf(id: Long): OpeningEntity? {
        return repo.findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(id, System.currentTimeMillis()).orElse(null)
    }

    open fun findNextStartDateOf(id: Long): Long? {
        val opening: Optional<OpeningEntity> = repo.findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(id, System.currentTimeMillis())
        return opening.map(OpeningEntity::dateStart).orElse(null)
    }

    open fun getOne(openingId: Long): OpeningEntity {
        return repo.getOne(openingId)
    }

    open fun delete(oe: OpeningEntity) {
        repo.delete(oe)
    }

    open fun saveTimeWindow(tw: TimeWindowEntity) {
        twRepo.save(tw)
    }
}
