package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ReviewRepository
import hu.kirdev.schpincer.model.ReviewEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
open class ReviewService {

    @Autowired
    private lateinit var repo: ReviewRepository

    fun findAll(): List<ReviewEntity> {
        return repo.findAll()
    }

    fun save(review: ReviewEntity) {
        repo.save(review)
    }

    fun findAll(circleId: Long): List<ReviewEntity> {
        return repo.findAllByCircle_Id(circleId)
    }
}
