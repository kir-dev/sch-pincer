package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.dto.ItemEntityDto
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OpeningEntity
import hu.kirdev.schpincer.service.*
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
open class ApiController {

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

    @ApiOperation("Item info")
    @GetMapping("/item/{id}")
    @ResponseBody
    fun getItem(request: HttpServletRequest, @PathVariable id: Long): ItemEntityDto? {
        val item = items.getOne(id)
        if (item == null || (!request.hasUser() && !item.visibleWithoutLogin))
            return null
        val loggedIn = request.hasUser() || request.isInInternalNetwork()
        return ItemEntityDto(item, openings.findNextOf(item.circle!!.id!!), loggedIn)
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
                                cache.computeIfAbsent(item.circle!!.id!!) { openings.findNextOf(it) },
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
                            cache.computeIfAbsent(item.circle!!.id!!) { openings.findNextOf(it) },
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
                .map { item: ItemEntity ->
                    ItemEntityDto(item, 
                            cache.computeIfAbsent(item.circle!!.id!!) { openings.findNextOf(it) },
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
                .map { item: ItemEntity ->
                    ItemEntityDto(item, 
                            cache.computeIfAbsent(item.circle!!.id!!) { openings.findNextOf(it) },
                            loggedIn || request.isInInternalNetwork())
                }
                .collect(Collectors.toList())
        return ResponseEntity(list, HttpStatus.OK)
    }

    @ApiOperation("Page of items")
    @GetMapping("/items/{page}")
    @ResponseBody
    fun getItems(request: HttpServletRequest, @PathVariable page: Int): ResponseEntity<List<ItemEntityDto>> {
        val loggedIn = request.hasUser()
        val cache: MutableMap<Long, OpeningEntity?> = HashMap()
        val list = items.findAll(page).stream()
                .filter { it.visibleWithoutLogin || loggedIn }
                .filter(ItemEntity::visibleInAll)
                .map { item: ItemEntity ->
                    ItemEntityDto(item, 
                            cache.computeIfAbsent(item.circle!!.id!!) { openings.findNextOf(it) },
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

    @ApiOperation("New order")
    @PostMapping("/order")
    @ResponseBody
    @Throws(Exception::class)
    fun newOrder(request: HttpServletRequest,
                 @RequestParam(required = true) id: Long,
                 @RequestParam(required = true) time: Int,
                 @RequestParam(required = true) comment: String,
                 @RequestParam(defaultValue = "1") count: Int,
                 @RequestParam(required = true) detailsJson: String
    ): ResponseEntity<String> {
        return orders.makeOrder(request, id, count, time.toLong(), comment, detailsJson)
    }

    @ApiOperation("Set room code")
    @PostMapping("/user/room")
    @ResponseBody
    fun setRoom(request: HttpServletRequest, @RequestParam(required = true) room: String): String {
        return try {
            request.session.setAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME, users.setRoom(request.getUserId(), room))
            "ACK"
        } catch (e: Exception) {
            "REJECT"
        }
    }

    @ApiOperation("Hased user id")
    @GetMapping("/user/id")
    @ResponseBody
    fun setRoom(request: HttpServletRequest): String {
        return request.getUserIfPresent()?.uid?.sha256() ?: "ERROR"
    }

    @Deprecated("There is an issue with allowed item counts, DO NOT remove this functionallity")
    @ApiOperation("Delete order")
    @PostMapping("/order/delete")
    @ResponseBody
    fun deleteOrder(request: HttpServletRequest, @RequestParam(required = true) id: Long): ResponseEntity<String> {
        return orders.cancelOrder(request, id)
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

}
