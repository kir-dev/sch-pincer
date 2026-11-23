package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.dao.UserRepository
import hu.kirdev.schpincer.model.OrderStatus
import hu.kirdev.schpincer.model.ReviewEntity
import hu.kirdev.schpincer.service.CircleService
import hu.kirdev.schpincer.service.OrderService
import hu.kirdev.schpincer.service.RealtimeConfigService
import hu.kirdev.schpincer.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.security.core.Authentication

@Controller
open class ReviewController(
    private val userRepository: UserRepository,
    private val orders: OrderService,
    private val circles: CircleService,
    private val reviews: ReviewService,
    private val config: RealtimeConfigService,
){

    @Operation(summary = "Review order page")
    @GetMapping("/review/{orderId}")
    fun rateOrder(@PathVariable orderId: Long, auth: Authentication?, model: Model): String? {
        val order = orders.getOne(orderId)
        val circleId = orders.getCircleIdByOrderId(orderId)
        if (!auth.hasUser() || order == null || order.status != OrderStatus.SHIPPED || circleId == null || order.reviewId != null) {
            throw Exception("Requirements before reviewing order are not met!")
        }

        model.addAttribute("selectedCircle", circles.getOne(circleId))
        model.addAttribute("orderId", orderId)
        model.addAttribute("review", ReviewEntity(rateOverAll = 3, ratePrice = 3, rateQuality = 3, rateSpeed = 3))
        model.addAttribute("circles", circles.findAllForMenu())
        config.injectPublicValues(model)
        return "orderReview"
    }

    @Operation(summary = "Send review of order")
    @PostMapping("/review/{orderId}")
    fun rateOrder(@PathVariable orderId: Long,
                  @RequestParam review: String?,
                  @RequestParam(required = true) rateQuality: Int,
                  @RequestParam(required = true) ratePrice: Int,
                  @RequestParam(required = true) rateSpeed: Int,
                  @RequestParam(required = true) rateOverAll: Int,
                  auth: Authentication?
    ): String {
        val order = orders.getOne(orderId)
        if (!auth.hasUser() || order == null || order.status != OrderStatus.SHIPPED || order.reviewId != null) {
            throw Exception("Requirements before reviewing order are not met!")
        }
        val user = auth.getUser(userRepository)!!
        reviews.createReview(user, orderId, review ?: "", rateQuality, ratePrice, rateSpeed, rateOverAll)
        return "redirect:/profile"
    }
}
