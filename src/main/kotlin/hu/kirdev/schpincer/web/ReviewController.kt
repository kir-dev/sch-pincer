package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.model.OrderStatus
import hu.kirdev.schpincer.model.ReviewEntity
import hu.kirdev.schpincer.service.CircleService
import hu.kirdev.schpincer.service.OrderService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

@Controller
open class ReviewController {
    @Autowired
    private lateinit var orders: OrderService

    @Autowired
    private lateinit var circles: CircleService

    @ApiOperation("Review order page")
    @GetMapping("/review/{orderId}")
    fun rateOrder(@PathVariable orderId: Long, request: HttpServletRequest, model: Model): String {
        val order = orders.getOne(orderId)
        val circleId = orders.getCircleIdByOrderId(orderId)
        if (!request.hasUser() || order == null || order.status != OrderStatus.SHIPPED || circleId == null) {
            return "redirect:/profile"
        }

        model.addAttribute("selectedCircle", circles.getOne(circleId))
        model.addAttribute("orderId", orderId)
        model.addAttribute("review", ReviewEntity(rateOverAll = 3, ratePrice = 3, rateQuality = 3, rateSpeed = 3))
        model.addAttribute("circles", circles.findAllForMenu())
        return "orderReview"
    }

    @ApiOperation("Send review of order")
    @PostMapping("/review")
    //@ResponseBody
    fun rateOrder(request: HttpServletRequest, @RequestParam(required = true) orderId: Long) {
        val order = orders.getOne(orderId)
        if (!request.hasUser() || order == null || order.status != OrderStatus.SHIPPED) {
            //return null
        }
        //3. should I return anything? a string with ERROR if validation fails?
    }
}