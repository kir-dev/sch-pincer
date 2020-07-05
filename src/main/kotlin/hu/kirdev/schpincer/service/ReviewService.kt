package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ReviewRepository
import hu.kirdev.schpincer.model.ReviewEntity
import hu.kirdev.schpincer.web.getUserIfPresent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Service
@Transactional
open class ReviewService {

    @Autowired
    private lateinit var repo: ReviewRepository

    @Autowired
    private lateinit var orders: OrderService

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var openings: OpeningService

    open fun findAll(): List<ReviewEntity> {
        return repo.findAll()
    }

    open fun save(review: ReviewEntity) {
        repo.save(review)
    }

    open fun findAll(circleId: Long): List<ReviewEntity> {
        return repo.findAllByCircle_IdOrderByDateDesc(circleId)
    }

    open fun createReview(request: HttpServletRequest, orderId: Long, review: String?, rateQuality: Int, ratePrice: Int,
                         rateSpeed: Int, rateOverAll: Int)
    {
        val circleId = orders.getCircleIdByOrderId(orderId)!!
        val user = request.getUserIfPresent()!!
        val order = orders.getOne(orderId)

        val fullReview = ReviewEntity(
                circle = circles.getOne(circleId),
                order = order,
                openingFeeling = openings.getOne(order?.openingId!!).feeling,
                userName = user.name,
                review = review,
                rateSpeed = rateSpeed,
                rateQuality = rateQuality,
                ratePrice = ratePrice,
                rateOverAll = rateOverAll,
                date = System.currentTimeMillis()
        )
        save(fullReview)
        orders.reviewOrder(orderId, fullReview.id!!)
    }
}
