package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.CircleRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.dto.CircleEntityInfoDto
import hu.kirdev.schpincer.dto.RoleEntryDto
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.web.sha256
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class CircleService {

    @Autowired
    private lateinit var repo: CircleRepository

    @Autowired
    private lateinit var openings: OpeningRepository

    @Transactional(readOnly = true)
    open fun findAll(pageable: Pageable): Page<CircleEntity> {
        return repo.findAll(pageable)
    }

    @Transactional(readOnly = true)
    open fun findAll(): List<CircleEntity> {
        return repo.findAll()
    }

    @Transactional(readOnly = true)
    open fun findAllForMenu(): List<CircleEntity> {
        return repo.findAllByVisibleTrueOrderByHomePageOrder()
    }

    @Transactional(readOnly = false)
    open fun save(circleEntity: CircleEntity) {
        repo.save(circleEntity)
    }

    private fun internalIncreaseInterestAndSave(circleEntity: CircleEntity): Long {
        val newInterest = circleEntity.interestCounter;
        repo.save(circleEntity)
        return newInterest
    }

    @Transactional(readOnly = false)
    open fun increaseInterest(id: Long): Long? {
        val circleEntity = getOne(id)
        if (circleEntity == null) {
            return null // TODO throw error?
        }
        return internalIncreaseInterestAndSave(circleEntity)
    }

    @Transactional(readOnly = false)
    open fun increaseInterestByAlias(alias: String): Long? {
        val circleEntity = findByAlias(alias)
        if (circleEntity == null) {
            return null // TODO throw error?
        }
        return internalIncreaseInterestAndSave(circleEntity)
    }

    @Transactional(readOnly = true)
    open fun getOne(id: Long): CircleEntity? {
        return repo.getOne(id)
    }

    @Transactional(readOnly = false)
    open fun delete(ce: CircleEntity) {
        repo.delete(ce)
    }

    @Transactional(readOnly = true)
    open fun findByAlias(alias: String): CircleEntity {
        return repo.findAllByAlias(alias)[0]
    }

    @Transactional(readOnly = true)
    open fun findByVirGroupId(id: Long): CircleEntity? {
        return repo.findOneByVirGroupId(id)
    }

    @Transactional(readOnly = true)
    open fun findAllForInfo(): List<CircleEntityInfoDto> {
        return repo.findAllByVisibleTrueOrderByHomePageOrder().asSequence()
                .map { CircleEntityInfoDto(it) }
                .onEach {
                    it.nextOpening = openings
                            .findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(
                                    it.circleEntity.id, System.currentTimeMillis())
                            .orElse(null)
                }
                .toList()
    }

}