package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.dto.KitchenOrderDto
import hu.kirdev.schpincer.model.CardType
import hu.kirdev.schpincer.model.OrderStatus
import hu.kirdev.schpincer.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import jakarta.servlet.http.HttpServletRequest

@Controller
class KitchenViewController {

    @Autowired
    private lateinit var users: UserService

    @Autowired
    private lateinit var openings: OpeningService

    @Autowired
    private lateinit var orders: OrderService

    @GetMapping("/kitchen-view/{circleId}/{openingId}/hand-over")
    fun handOver(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            model: Model,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/profile"

        model.addAttribute("openingId", openingId)
        model.addAttribute("circleId", circleId)
        return "kitchen/handOver"
    }

    @GetMapping("/kitchen-view/{circleId}/{openingId}/kitchen")
    fun kitchen(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            model: Model,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/profile"

        model.addAttribute("openingId", openingId)
        model.addAttribute("circleId", circleId)
        return "kitchen/kitchen"
    }

    @GetMapping("/kitchen-view/{circleId}/{openingId}/merged")
    fun merged(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            model: Model,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/profile"

        model.addAttribute("openingId", openingId)
        model.addAttribute("circleId", circleId)
        return "kitchen/merged"
    }

    @GetMapping("/kitchen-view/{circleId}/{openingId}/shipping")
    fun shipping(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            model: Model,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/profile"

        model.addAttribute("openingId", openingId)
        model.addAttribute("circleId", circleId)
        return "kitchen/shipping"
    }


    @GetMapping("/kitchen-view/{circleId}/{openingId}/shipped")
    fun shipped(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            model: Model,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/profile"

        model.addAttribute("openingId", openingId)
        model.addAttribute("circleId", circleId)
        return "kitchen/shipped"
    }

    @GetMapping("/kitchen-view/{circleId}/{openingId}/new-order")
    fun newOrder(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            model: Model,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/profile"

        model.addAttribute("openingId", openingId)
        model.addAttribute("circleId", circleId)
        return "kitchen/newOrder"
    }

    @GetMapping("/kitchen-view/{circleId}/{openingId}/stats")
    fun stats(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            model: Model,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/profile"

        model.addAttribute("openingId", openingId)
        model.addAttribute("circleId", circleId)
        return "kitchen/stats"
    }

    @ResponseBody
    @RequestMapping(
            path = ["/api/kitchen-view/{circleId}/{openingId}/hand-over"],
            method = [RequestMethod.GET, RequestMethod.POST])
    fun fetchHandOver(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            request: HttpServletRequest
    ): List<KitchenOrderDto> {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return listOf()

        val openingIntervalLength = openings.getOne(openingId).intervalLength

        return orders.findToExport(openingId, OrderStrategy.ORDER_GROUPED.representation)
                .filter { it.status == OrderStatus.COMPLETED }
                .map { KitchenOrderDto(it, openingIntervalLength) }
    }

    @ResponseBody
    @RequestMapping(
            path = ["/api/kitchen-view/{circleId}/{openingId}/kitchen"],
            method = [RequestMethod.GET, RequestMethod.POST])
    fun fetchKitchen(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            request: HttpServletRequest
    ): List<KitchenOrderDto> {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return listOf()

        val openingIntervalLength = openings.getOne(openingId).intervalLength

        return orders.findToExport(openingId, OrderStrategy.ORDER_GROUPED.representation)
                .filter { it.status == OrderStatus.ACCEPTED
                        || it.status == OrderStatus.INTERPRETED
                        || it.status == OrderStatus.UNKNOWN }
                .map { KitchenOrderDto(it, openingIntervalLength) }
    }

    @ResponseBody
    @RequestMapping(
            path = ["/api/kitchen-view/{circleId}/{openingId}/merged"],
            method = [RequestMethod.GET, RequestMethod.POST])
    fun fetchMerged(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            request: HttpServletRequest
    ): List<KitchenOrderDto> {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return listOf()

        val openingIntervalLength = openings.getOne(openingId).intervalLength

        return orders.findToExport(openingId, OrderStrategy.ORDER_GROUPED.representation)
                .filter { it.status == OrderStatus.ACCEPTED
                        || it.status == OrderStatus.INTERPRETED
                        || it.status == OrderStatus.COMPLETED
                        || it.status == OrderStatus.UNKNOWN }
                .map { KitchenOrderDto(it, openingIntervalLength) }
    }

    @ResponseBody
    @RequestMapping(
            path = ["/api/kitchen-view/{circleId}/{openingId}/shipping"],
            method = [RequestMethod.GET, RequestMethod.POST])
    fun fetchShipping(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            request: HttpServletRequest
    ): List<KitchenOrderDto> {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return listOf()

        val openingIntervalLength = openings.getOne(openingId).intervalLength

        return orders.findToExport(openingId, OrderStrategy.ORDER_GROUPED.representation)
                .filter { it.status == OrderStatus.HANDED_OVER }
                .map { KitchenOrderDto(it, openingIntervalLength) }
    }

    @ResponseBody
    @RequestMapping(
            path = ["/api/kitchen-view/{circleId}/{openingId}/shipped"],
            method = [RequestMethod.GET, RequestMethod.POST])
    fun fetchShipped(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            request: HttpServletRequest
    ): List<KitchenOrderDto> {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return listOf()

        val openingIntervalLength = openings.getOne(openingId).intervalLength

        return orders.findToExport(openingId, OrderStrategy.ORDER_GROUPED.representation)
                .filter { it.status == OrderStatus.SHIPPED }
                .map { KitchenOrderDto(it, openingIntervalLength) }
    }

    @PostMapping("/api/kitchen-view/{circleId}/{openingId}/view/{view}/{orderId}/status/{status}")
    fun changeStatus(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            @PathVariable view: String,
            @PathVariable orderId: Long,
            @PathVariable status: String,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/api/kitchen-view/${circleId}/${openingId}/${view}"

        orders.updateStatus(orderId, status)

        return "redirect:/api/kitchen-view/${circleId}/${openingId}/${view}"
    }

    data class ChefCommentDto(var comment: String = "")

    @PostMapping("/api/kitchen-view/{circleId}/{openingId}/view/{view}/{orderId}/chef-comment")
    fun changeChefComment(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            @PathVariable view: String,
            @PathVariable orderId: Long,
            @RequestBody comment: ChefCommentDto,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/api/kitchen-view/${circleId}/${openingId}/${view}"

        orders.updateChefComment(orderId, comment.comment)

        return "redirect:/api/kitchen-view/${circleId}/${openingId}/${view}"
    }


    data class SearchTermsDto(var name: String = "", var room: String = "")

    data class UserSearchResultDto(var id: String = "", var name: String = "", var room: String = "",
                                   var uid: String = "", var card: CardType = CardType.DO)

    @ResponseBody
    @PostMapping("/api/kitchen-view/{circleId}/{openingId}/search")
    fun changeChefComment(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            @RequestBody searchTerms: SearchTermsDto,
            request: HttpServletRequest
    ): List<UserSearchResultDto> {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return listOf()

        return if (searchTerms.name.isNotBlank()) {
            users.findByUsernameContains(searchTerms.name).map { UserSearchResultDto(it.uid, it.name, it.room, it.uid.sha256().substring(0, 6), it.cardType) }
        } else if (searchTerms.room.isNotBlank()) {
            users.findByRoomContains(searchTerms.room).map { UserSearchResultDto(it.uid, it.name, it.room, it.uid.sha256().substring(0, 6), it.cardType) }
        } else {
            listOf()
        }
    }

    data class TimeWindowStatDto(
            var name: String,
            var date: Long,
            var acceptedCount: Int,
            var interpretedCount: Int,
            var completedCount: Int,
            var handedOverCount: Int,
            var shippedCount: Int,
            var rejectedCount: Int,
            var sum: Int,
    )

    @ResponseBody
    @PostMapping("/api/kitchen-view/{circleId}/{openingId}/stats")
    fun gatherStats(
            @PathVariable circleId: Long,
            @PathVariable openingId: Long,
            request: HttpServletRequest
    ): List<TimeWindowStatDto> {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return listOf()

        val intervals = orders.findAllByOpening(openingId)
                .groupBy { it.intervalId }
                .map {
                    TimeWindowStatDto(
                            it.value[0].intervalMessage,
                            it.value[0].date,
                            it.value.filter { it.status == OrderStatus.ACCEPTED }.sumOf { it.count },
                            it.value.filter { it.status == OrderStatus.INTERPRETED }.sumOf { it.count },
                            it.value.filter { it.status == OrderStatus.COMPLETED }.sumOf { it.count },
                            it.value.filter { it.status == OrderStatus.HANDED_OVER }.sumOf { it.count },
                            it.value.filter { it.status == OrderStatus.SHIPPED }.sumOf { it.count },
                            it.value.filter { it.status == OrderStatus.CANCELLED }.sumOf { it.count },
                            it.value.sumOf { it.count }
                    )
                }
                .sortedBy { it.date }
                .toMutableList()
        intervals.add(TimeWindowStatDto("TOTAL SUM", 0,
                intervals.sumOf { it.acceptedCount },
                intervals.sumOf { it.interpretedCount },
                intervals.sumOf { it.completedCount },
                intervals.sumOf { it.handedOverCount },
                intervals.sumOf { it.shippedCount },
                intervals.sumOf { it.rejectedCount },
                intervals.sumOf { it.sum }
                ))
        return intervals
    }

}
