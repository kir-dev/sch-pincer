package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ReviewRepository
import hu.kirdev.schpincer.model.ReviewEntity
import hu.kirdev.schpincer.model.UserEntity
import hu.kirdev.schpincer.web.getUserIfPresent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

@Service
open class ReviewService {

    @Autowired
    private lateinit var repo: ReviewRepository

    @Autowired
    private lateinit var orders: OrderService

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var openings: OpeningService

    @Transactional(readOnly = true)
    open fun findAll(): List<ReviewEntity> {
        return repo.findAll()
    }

    private fun save(review: ReviewEntity) {
        repo.save(review)
    }

    @Transactional(readOnly = true)
    open fun findAll(circleId: Long): List<ReviewEntity> {
        return repo.findAllByCircle_IdOrderByDateDesc(circleId)
    }

    @Transactional(readOnly = false)
    open fun createReview(user: UserEntity,
                          orderId: Long,
                          review: String,
                          rateQuality: Int,
                          ratePrice: Int,
                          rateSpeed: Int,
                          rateOverAll: Int
    ) {
        val circleId = orders.getCircleIdByOrderId(orderId)!!
        val order = orders.getOne(orderId)

        val fullReview = ReviewEntity(
                circle = circles.getOne(circleId),
                order = order,
                openingFeeling = openings.getOne(order?.openingId!!).feeling,
                userName = user.name,
                review = if (review.length > 1000) review.substring(0, 1000) else review,
                rateSpeed = rateSpeed.between(1, 5),
                rateQuality = rateQuality.between(1, 5),
                ratePrice = ratePrice.between(1, 5),
                rateOverAll = rateOverAll.between(1, 5),
                date = System.currentTimeMillis()
        )
        save(fullReview)
        orders.reviewOrder(orderId, fullReview.id)
    }
}

private fun Int.between(from: Int, to: Int) = Math.max(from, Math.min(to, this))
