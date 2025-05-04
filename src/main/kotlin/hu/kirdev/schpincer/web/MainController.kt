package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.model.CardType
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.model.OrderStatus
import hu.kirdev.schpincer.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import org.springframework.security.core.Authentication
import java.time.Instant

@Controller
open class MainController {

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var openings: OpeningService

    @Autowired
    private lateinit var orders: OrderService

    @Autowired
    private lateinit var timeService: TimeService

    @Autowired
    private lateinit var statService: StatisticsService

    @Autowired
    private lateinit var config: RealtimeConfigService

    @GetMapping("/")
    fun root(auth: Authentication?, model: Model, @RequestParam(defaultValue = "") error: String): String {
        val circlesList: List<CircleEntity> = circles.findAllForMenu()
        model.addAttribute("circles", circlesList)

        val random = ArrayList(circlesList)
        random.shuffle()
        model.addAttribute("circlesRandom", random)

        val opens = openings.findUpcomingOpenings()
        model.addAttribute("opener", if (opens.isNotEmpty()) opens[0] else null)
        model.addAttribute("openings", openings.findNextWeek())
        model.addAttribute("error", error)

        model.addAttribute("orders", Collections.EMPTY_LIST)
        if (auth.hasUser()) {
            model.addAttribute("orders", orders.findAll(auth.getUserId()!!)
                    .filter { it.status === OrderStatus.ACCEPTED }
                    .filter { it.date >= (Instant.now().toEpochMilli() - (1000 * 60 * 60 * 24 * 7 * 3)) }
                    .take(3))
        }
        model.addAttribute("timeService", timeService)
        config.injectPublicValues(model)
        return "index"
    }

    @GetMapping("/items")
    fun items(@RequestParam(name = "q", defaultValue = "") keyword: String, model: Model, auth: Authentication?): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("searchMode", "" != keyword)
        model.addAttribute("keyword", keyword)
        model.addAttribute("card", (auth.getUserIfPresent()?.cardType ?: CardType.DO).name)
        config.injectPublicValues(model)
        return "items"
    }

    @GetMapping("/falatozo")
    fun circle(model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circlesWithOpening", circles.findAllForInfo())
        model.addAttribute("timeService", timeService)
        config.injectPublicValues(model)
        return "circle"
    }

    @GetMapping("/circle/{circle}")
    fun circleSpecific(@PathVariable circle: String, model: Model, auth: Authentication?): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("card", (auth.getUserIfPresent()?.cardType ?: CardType.DO).name)
        if (circle.matches("^\\d+$".toRegex())) {
            val id = circle.toLong()
            model.addAttribute("selectedCircle", circles.getOne(id))
            model.addAttribute("nextOpening", openings.findNextStartDateOf(id))
        } else {
            val circleEntity: CircleEntity = circles.findByAlias(circle)
            model.addAttribute("selectedCircle", circleEntity)
            model.addAttribute("nextOpening", openings.findNextStartDateOf(circleEntity.id))
        }
        model.addAttribute("timeService", timeService)
        config.injectPublicValues(model)
        return "circleProfile"
    }

    @GetMapping(path = ["/provider/{circle}", "/p/{circle}"])
    fun circleSpecificAlias(@PathVariable circle: String, model: Model, auth: Authentication?): String {
        return circleSpecific(circle, model, auth)
    }

    data class OrderForDetails(
        val id: Long,
        val name: String,
        val count: Int,
        val price: Int,
        val image: String,
        val color: String,
        val room: String,
        val comment: String,
        val changable: Boolean
    )

    @GetMapping("/profile")
    fun profile(auth: Authentication?, model: Model): String {
        val orders = this.orders.findAll(auth.getUserId()!!)
        model.addAttribute("orders", orders)
        model.addAttribute("ordersForDetails", orders.map {
            OrderForDetails(
                it.id, it.name, it.count, it.price,
                it.orderedItem?.imageName ?: "/image/blank-item.jpg",
                it.orderedItem?.circle?.cssClassName ?: "blank",
                it.room,
                it.comment.replace(Regex("^\\[((AB)|(KB)|(DO))] "), ""),
                it.cancelUntil > Instant.now().toEpochMilli() && it.status == OrderStatus.ACCEPTED
            )
        })
        model.addAttribute("priceBreakdowns", this.orders.generatePriceBreakdowns(orders))
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("timeService", timeService)
        model.addAttribute("uid", auth.getUserId()!!.sha256().substring(0, 6))
        config.injectPublicValues(model)
        return "profile"
    }

    private val statsViews: ConcurrentHashMap<String, String> = ConcurrentHashMap<String, String>()

    @GetMapping("/stats")
    fun stats(auth: Authentication?, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        val user = auth.getUser()
        statsViews.computeIfPresent(user.uid) { _, b -> "$b+1" }
        statsViews.computeIfAbsent(user.uid) { user.name + ";" + Instant.now().toEpochMilli() + ";1" }
        statService.getDetailsForUser(user).entries.forEach { model.addAttribute(it.key, it.value) }
        config.injectPublicValues(model)
        return "stats"
    }

    @GetMapping("/admin/stats-insight")
    @ResponseBody
    fun statsInsights(auth: Authentication?): String {
        return if (auth.getUserIfPresent()?.sysadmin == true) {
            statsViews.values.toString()
        } else {
            "Nice try!"
        }
    }

}
