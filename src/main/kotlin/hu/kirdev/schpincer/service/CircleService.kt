package hu.kirdev.schpincer.service

import hu.gerviba.webschop.dto.CircleEntityInfoDto
import hu.kirdev.schpincer.dao.CircleRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.model.CircleEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
open class CircleService {

    @Autowired
    private lateinit var repo: CircleRepository

    @Autowired
    private lateinit var openings: OpeningRepository

    open fun findAll(pageable: Pageable): Page<CircleEntity> {
        return repo.findAll(pageable)
    }

    open fun findAll(): List<CircleEntity> {
        return repo.findAll()
    }

    open fun findAllForMenu(): List<CircleEntity> {
        return repo.findAllByVisibleTrueOrderByHomePageOrder()
    }

    open fun save(circleEntity: CircleEntity) {
        repo.save(circleEntity)
    }

    open fun getOne(id: Long): CircleEntity? {
        return repo.getOne(id)
    }

    open fun delete(ce: CircleEntity) {
        repo.delete(ce)
    }

    open fun findByAlias(alias: String): CircleEntity {
        return repo.findAllByAlias(alias)[0]
    }

    open fun findAllForInfo(): List<CircleEntityInfoDto> {
        return repo.findAllByVisibleTrueOrderByHomePageOrder().asSequence()
                .map { CircleEntityInfoDto(it) }
                .onEach { it.nextOpening = openings
                        .findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(
                                it.circleEntity.id!!, System.currentTimeMillis())
                        .orElse(null) }
                .toList()
    }

}