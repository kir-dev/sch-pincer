package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.model.OrderStatus
import hu.kirdev.schpincer.model.ReviewEntity
import hu.kirdev.schpincer.service.CircleService
import hu.kirdev.schpincer.service.OrderService
import hu.kirdev.schpincer.service.ReviewService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.lang.Exception
import javax.servlet.http.HttpServletRequest

@Controller
open class ReviewController {
    @Autowired
    private lateinit var orders: OrderService

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var reviews: ReviewService

    @ApiOperation("Review order page")
    @GetMapping("/review/{orderId}")
    fun rateOrder(@PathVariable orderId: Long, request: HttpServletRequest, model: Model): String? {
        val order = orders.getOne(orderId)
        val circleId = orders.getCircleIdByOrderId(orderId)
        if (!request.hasUser() || order == null || order.status != OrderStatus.SHIPPED || circleId == null || order.reviewId != null) {
            throw Exception("Requirements before reviewing order are not met!")
        }

        model.addAttribute("selectedCircle", circles.getOne(circleId))
        model.addAttribute("orderId", orderId)
        model.addAttribute("review", ReviewEntity(rateOverAll = 3, ratePrice = 3, rateQuality = 3, rateSpeed = 3))
        model.addAttribute("circles", circles.findAllForMenu())
        return "orderReview"
    }

    @ApiOperation("Send review of order")
    @PostMapping("/review/{orderId}")
    fun rateOrder(@PathVariable orderId: Long,
                  @RequestParam review: String?,
                  @RequestParam(required = true) rateQuality: Int,
                  @RequestParam(required = true) ratePrice: Int,
                  @RequestParam(required = true) rateSpeed: Int,
                  @RequestParam(required = true) rateOverAll: Int,
                  request: HttpServletRequest): String?
    {
        val order = orders.getOne(orderId)
        if (!request.hasUser() || order == null || order.status != OrderStatus.SHIPPED || order.reviewId != null) {
            throw Exception("Requirements before reviewing order are not met!")
        }
        reviews.createReview(request, orderId, review, rateQuality, ratePrice, rateSpeed, rateOverAll)
        return "redirect:/profile"
    }
}