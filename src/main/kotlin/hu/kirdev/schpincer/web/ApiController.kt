package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.dto.ItemEntityDto
import hu.kirdev.schpincer.dto.ManualUserDetails
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OpeningEntity
import hu.kirdev.schpincer.service.*
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
open class ApiController {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var openings: OpeningService

    @Autowired
    private lateinit var items: ItemService

    @Autowired
    private lateinit var users: UserService

    @Autowired
    private lateinit var orders: OrderService

    @Autowired
    private lateinit var timeService: TimeService

    @Value("\${schpincer.indualsch-api-token:}")
    private lateinit var indulaschApiToken: String

    @Value("\${schpincer.api.base-url}")
    private lateinit var baseUrl: String

    @ApiOperation("Item info")
    @GetMapping("/item/{id}")
    @ResponseBody
    fun getItem(request: HttpServletRequest, @PathVariable id: Long): ItemEntityDto? {
        val item = items.getOne(id)
        if (item == null || (!request.hasUser() && !item.visibleWithoutLogin))
            return null
        val loggedIn = request.hasUser() || request.isInInternalNetwork()
        return ItemEntityDto(item, openings.findNextOf(item.circle!!.id), loggedIn)
    }

    @ApiOperation("List of items")
    @GetMapping("/items")
    @ResponseBody
    fun getAllItems(
            request: HttpServletRequest,
            @RequestParam(required = false) circle: Long?
    ): ResponseEntity<List<ItemEntityDto>> {
        val cache: MutableMap<Long, OpeningEntity?> = HashMap()
        val loggedIn = request.hasUser()
        if (circle != null) {
            val list = items.findAllByCircle(circle).stream()
                    .filter { it.visibleWithoutLogin || loggedIn }
                    .filter(ItemEntity::visible)
                    .map { item: ItemEntity ->
                        ItemEntityDto(item,
                                cache.computeIfAbsent(item.circle!!.id) { openings.findNextOf(it) },
                                loggedIn || request.isInInternalNetwork())
                    }
                    .collect(Collectors.toList())
            return ResponseEntity(list, HttpStatus.OK)
        }
        val list = items.findAll().stream()
                .filter { it.visibleWithoutLogin || loggedIn }
                .filter(ItemEntity::visibleInAll)
                .filter(ItemEntity::visible)
                .map { item: ItemEntity ->
                    ItemEntityDto(item,
                            cache.computeIfAbsent(item.circle!!.id) { openings.findNextOf(it) },
                            loggedIn || request.isInInternalNetwork())
                }
                .collect(Collectors.toList())
        return ResponseEntity(list, HttpStatus.OK)
    }

    @ApiOperation("List of items orderable right now")
    @GetMapping("/items/now")
    @ResponseBody
    fun getAllItemsToday(request: HttpServletRequest): ResponseEntity<List<ItemEntityDto>> {
        val loggedIn = request.hasUser()
        val cache: MutableMap<Long, OpeningEntity?> = HashMap()
        val list = items.findAllByOrderableNow().stream()
                .filter { it.visibleWithoutLogin || loggedIn }
                .filter(ItemEntity::visibleInAll)
                .filter(ItemEntity::visible)
                .map { item: ItemEntity ->
                    ItemEntityDto(item,
                            cache.computeIfAbsent(item.circle!!.id) { openings.findNextOf(it) },
                            loggedIn || request.isInInternalNetwork())
                }
                .collect(Collectors.toList())
        return ResponseEntity(list, HttpStatus.OK)
    }

    @ApiOperation("List of items orderable tomorrow")
    @GetMapping("/items/tomorrow")
    @ResponseBody
    fun getAllItemsTomorrow(request: HttpServletRequest): ResponseEntity<List<ItemEntityDto>> {
        val loggedIn = request.hasUser()
        val cache: MutableMap<Long, OpeningEntity?> = HashMap()
        val list = items.findAllByOrerableTomorrow().stream()
                .filter { it.visibleWithoutLogin || loggedIn }
                .filter(ItemEntity::visibleInAll)
                .filter(ItemEntity::visible)
                .map { item: ItemEntity ->
                    ItemEntityDto(item,
                            cache.computeIfAbsent(item.circle!!.id) { openings.findNextOf(it) },
                            loggedIn || request.isInInternalNetwork())
                }
                .collect(Collectors.toList())
        return ResponseEntity(list, HttpStatus.OK)
    }

// Disabled due to previous change in handling items
//    @ApiOperation("Page of items")
//    @GetMapping("/items/{page}")
//    @ResponseBody
    fun getItems(request: HttpServletRequest, @PathVariable page: Int): ResponseEntity<List<ItemEntityDto>> {
        val loggedIn = request.hasUser()
        val cache: MutableMap<Long, OpeningEntity?> = HashMap()
        val list = items.findAll(page).stream()
                .filter { it.visibleWithoutLogin || loggedIn }
                .filter(ItemEntity::visibleInAll)
                .map { item: ItemEntity ->
                    ItemEntityDto(item,
                            cache.computeIfAbsent(item.circle!!.id) { openings.findNextOf(it) },
                            loggedIn || request.isInInternalNetwork())
                }
                .collect(Collectors.toList())
        return ResponseEntity(list, HttpStatus.OK)
    }

    @ApiOperation("List of openings")
    @GetMapping("/openings")
    @ResponseBody
    fun getAllOpenings(): ResponseEntity<List<OpeningEntity>> {
        val page = openings.findAll()
        return ResponseEntity(page, HttpStatus.OK)
    }

    @ApiOperation("List of openings (next week period)")
    @GetMapping("/openings/week")
    @ResponseBody
    fun getNextWeekOpenings(): ResponseEntity<List<OpeningEntity>> {
        val page = openings.findNextWeek()
        return ResponseEntity(page, HttpStatus.OK)
    }

    @ApiOperation("List of circles")
    @GetMapping("/circles")
    @ResponseBody
    fun getAllCircles(): ResponseEntity<List<CircleEntity>> {
        val page = circles.findAll()
        return ResponseEntity(page, HttpStatus.OK)
    }

    data class NewOrderRequest(var id: Long = -1,
                               var time: Int = -1,
                               var comment: String = "",
                               var count: Int = 1,
                               var detailsJson: String = "{}",
                               var manualOrderDetails: ManualUserDetails? = null
    )

    @ApiOperation("New order")
    @PostMapping("/order")
    @ResponseBody
    @Throws(Exception::class)
    fun newOrder(request: HttpServletRequest, @RequestBody requestBody: NewOrderRequest): ResponseEntity<String> {
        if (requestBody.id < 0 || requestBody.time < 0 || requestBody.detailsJson == "{}")
            return ResponseEntity(RESPONSE_INTERNAL_ERROR, HttpStatus.OK)
        val user = request.getUserIfPresent() ?: return responseOf("Error 403", HttpStatus.FORBIDDEN)
        try {
            if (requestBody.manualOrderDetails != null) {
                log.info("{}:{} is making a manual order with details: {}, for {}",
                        user.name, user.uid, requestBody.detailsJson, requestBody.manualOrderDetails?.toString() ?: "null")
                return orders.makeManualOrder(user, requestBody.id, requestBody.count, requestBody.time.toLong(),
                        requestBody.comment, requestBody.detailsJson, requestBody.manualOrderDetails!!)
            } else {
                return orders.makeOrder(user, requestBody.id, requestBody.count, requestBody.time.toLong(),
                        requestBody.comment, requestBody.detailsJson)
            }
        } catch (e: FailedOrderException) {
            log.warn("Failed to make new order by '${request.getUserIfPresent()?.uid ?: "n/a"}' reason: ${e.response}")
            return responseOf(e.response)
        }
    }

    data class RoomChangeRequest(var room: String = "")

    @ApiOperation("Set room code")
    @PostMapping("/user/room")
    @ResponseBody
    fun setRoom(request: HttpServletRequest, @RequestBody(required = true) requestBody: RoomChangeRequest): String {
        return try {
            request.session.setAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME, users.setRoom(request.getUserId(), requestBody.room))
            "ACK"
        } catch (e: Exception) {
            "REJECT"
        }
    }

    data class DeleteRequestDto(var id: Long = 0)

    @Deprecated("There is an issue with allowed item counts, DO NOT remove this functionallity")
    @ApiOperation("Delete order")
    @PostMapping("/order/delete")
    @ResponseBody
    fun deleteOrder(request: HttpServletRequest, @RequestBody(required = true) body: DeleteRequestDto): ResponseEntity<String> {
        val user = request.getUserIfPresent() ?: return responseOf("Error 403", HttpStatus.FORBIDDEN)
        try {
            return orders.cancelOrder(user, body.id)
        } catch (e: FailedOrderException) {
            log.warn("Failed to cancel order by '${request.getUserIfPresent()?.uid ?: "n/a"}' reason: ${e.response}")
            return responseOf(e.response)
        }

    }

    @GetMapping("/version")
    @ResponseBody
    fun version(): String {
        return "Version: " + javaClass.getPackage().implementationVersion
    }

    @GetMapping("/time")
    @ResponseBody
    fun time(): String {
        return "Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(System.currentTimeMillis())}\n" +
                "Timestamp: ${System.currentTimeMillis()}"
    }

    data class OpeningDetail(var name: String, var icon: String, var available: Int, var outOf: Int, var comment: String)

    @GetMapping("/openings-for-indulasch")
    @ResponseBody
    fun openingsApi(@RequestParam(required = false) token: String?): List<OpeningDetail> {
        if (token.isNullOrBlank() || !token.equals(indulaschApiToken))
            return listOf()

        return openings.findNextWeek()
                .filter { it.circle != null }
                .map { OpeningDetail(
                        name = it.circle?.displayName ?: "n/a",
                        icon =  baseUrl + (it.circle?.logoUrl ?: ""),
                        available = Math.max(0, Math.min(
                                        it.timeWindows.sumOf { tw -> tw.normalItemCount },
                                        it.maxOrder - it.timeWindows.sumOf { tw -> it.maxOrderPerInterval - tw.normalItemCount
                                })),
                        outOf = it.maxOrder,
                        comment = "Rendelhető ${timeService.format(it.orderEnd, "MM.dd. HH:mm")}-ig az sch-pincéren"
                ) }
    }

    private val trashpandaVoters: ConcurrentHashMap<String, Int> = ConcurrentHashMap<String, Int>()

//    @PostMapping("/easteregg/trashpanda/{feedback}")
//    @ResponseBody
    fun trashpandaVote(
            request: HttpServletRequest,
            @PathVariable feedback: Int
    ): String {
        val ip = request.remoteAddr ?: ""
        if (trashpandaVoters.containsKey(ip)) {
            if (feedback == 1 || feedback == 2)
                trashpandaVoters[ip] = feedback
        } else {
            trashpandaVoters[ip] = feedback
        }
        return "OK"
    }

//    @GetMapping("/admin/trashpanda")
//    @ResponseBody
    fun trashpandaShow(request: HttpServletRequest): String {
        if (request.getUserIfPresent()?.sysadmin == true) {
            return "Good: " + trashpandaVoters.values.count { it == 1 } +
                    " Bad: " + trashpandaVoters.values.count { it == 2 } +
                    " OK: " + trashpandaVoters.values.count { it == 3 } +
                    " Side: " + trashpandaVoters.values.count { it == 4 } +
                    " HACK: " + trashpandaVoters.values.count { it < 1 || it > 4 }
        } else {
            return "Nice try!"
        }
    }

//    @GetMapping("/admin/trashpanda/raw")
//    @ResponseBody
    fun trashpandaShowRaw(request: HttpServletRequest): String {
        if (request.getUserIfPresent()?.sysadmin == true) {
            return trashpandaVoters.entries.toString()
        } else {
            return "Nice try!"
        }
    }
}
