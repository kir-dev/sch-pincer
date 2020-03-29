package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.model.ItemEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000
const val HOUR6_IN_MILLIS = 6 * 60 * 60 * 1000

@Service
@Transactional
open class ItemService {

    @Autowired
    private lateinit var repo: ItemRepository

    @Autowired
    private lateinit var  openingRepo: OpeningRepository

    open fun findAll(): List<ItemEntity> {
        return repo.findAllByVisibleTrueOrderByPrecedenceDesc()
    }

    open fun findAll(page: Int): List<ItemEntity> {
        return repo.findAllByVisibleTrueAndVisibleInAllTrueOrderByPrecedenceDesc(PageRequest.of(page, 6)).getContent()
    }

    open fun getOne(itemId: Long): ItemEntity? {
        return repo.getOne(itemId)
    }

    open fun save(itemEntity: ItemEntity) {
        repo.save(itemEntity)
    }

    open fun findAllByCircle(circleId: Long): List<ItemEntity> {
        return repo.findAllByCircle_IdOrderByManualPrecedenceDesc(circleId)
    }

    open fun delete(ie: ItemEntity) {
        repo.delete(ie)
    }

    open fun deleteByCircle(circleId: Long) {
        repo.deleteByCircle_Id(circleId)
    }

    open fun findAllByOrderableNow(): List<ItemEntity> {
        val time = System.currentTimeMillis()
        val circles = openingRepo.findAllByOrderStartLessThanAndOrderEndGreaterThan(time, time)
                .map{ opening -> opening.circle?.id!! }
        return repo.findAllByCircle_IdInOrderByPrecedenceDesc(circles)
    }

    open fun findAllByOrerableTomorrow(): List<ItemEntity> {
        val time1 = getJustDateFrom(System.currentTimeMillis()) + DAY_IN_MILLIS
        val time2 = time1 + DAY_IN_MILLIS + HOUR6_IN_MILLIS
        val circles = openingRepo.findAllByOrderStartGreaterThanAndOrderStartLessThan(time1, time2)
                .map{ opening -> opening.circle?.id!! }
        return repo.findAllByCircle_IdInOrderByPrecedenceDesc(circles)
    }

    open fun getJustDateFrom(millis: Long): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = millis
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        c[Calendar.MILLISECOND] = 0
        return c.timeInMillis
    }

}
