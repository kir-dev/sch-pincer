package hu.kirdev.schpincer.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import hu.kirdev.schpincer.dto.ItemEntityDto
import hu.kirdev.schpincer.dto.ManualUserDetails
import hu.kirdev.schpincer.model.ItemCategory
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OpeningEntity
import hu.kirdev.schpincer.model.OrderStatus
import hu.kirdev.schpincer.service.*
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.stream.Collectors
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import java.time.Instant
import kotlin.math.round

@RestController
@RequestMapping("/api")
open class ApiController(
        private val circles: CircleService,
        private val openings: OpeningService,
        private val items: ItemService,
        private val users: UserService,
        private val orders: OrderService,
        private val timeService: TimeService,
        @Value("\${schpincer.api-tokens:}")
        apiTokensRaw: String,

        @param:Value("\${schpincer.api.base-url}")
        private val baseUrl: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val apiTokens = apiTokensRaw.split(Regex(", *"))

    @Operation(summary = "Item info")
    @GetMapping("/item/{id}")
    @ResponseBody
    fun getItem(
            request: HttpServletRequest,
            @PathVariable id: Long,
            @RequestParam(defaultValue = "0") explicitOpening: Long,
            auth: Authentication?,
    ): ItemEntityDto? {
        val item = items.getOne(id)
        if (item == null || (auth?.isAuthenticated != true && !item.visibleWithoutLogin))
            return null
        val loggedIn = auth?.isAuthenticated == true || request.isInInternalNetwork()
        val opening = if (explicitOpening != 0L) openings.getOne(explicitOpening) else openings.findNextOf(item.circle!!.id)
        return ItemEntityDto(item, opening, loggedIn, loggedIn && explicitOpening > 0)
    }

    @Operation(summary = "List of items")
    @GetMapping("/items")
    @ResponseBody
    fun getAllItems(
            request: HttpServletRequest,
            @RequestParam(required = false) circle: Long?,
            auth: Authentication?,
    ): ResponseEntity<List<ItemEntityDto>> {
        val cache: MutableMap<Long, OpeningEntity?> = HashMap()
        val loggedIn = auth?.isAuthenticated == true || request.isInInternalNetwork()
        if (circle != null) {
            val list = items.findAllByCircle(circle).stream()
                    .filter { it.visibleWithoutLogin || loggedIn }
                    .filter(ItemEntity::visible)
                    .map { item: ItemEntity ->
                        ItemEntityDto(item,
                                cache.computeIfAbsent(item.circle!!.id) { openings.findNextOf(it) },
                                loggedIn || request.isInInternalNetwork(),
                                false)
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
                            loggedIn || request.isInInternalNetwork(),
                            false)
                }
                .collect(Collectors.toList())
        return ResponseEntity(list, HttpStatus.OK)
    }

    @Operation(summary = "List of items orderable right now")
    @GetMapping("/items/now")
    @ResponseBody
    fun getAllItemsToday(request: HttpServletRequest, auth: Authentication?): ResponseEntity<List<ItemEntityDto>> {
        val loggedIn = auth?.isAuthenticated == true || request.isInInternalNetwork()
        val cache: MutableMap<Long, OpeningEntity?> = HashMap()
        val list = items.findAllByOrderableNow().stream()
                .filter { it.visibleWithoutLogin || loggedIn }
                .filter(ItemEntity::visibleInAll)
                .filter(ItemEntity::visible)
                .map { item: ItemEntity ->
                    ItemEntityDto(item,
                            cache.computeIfAbsent(item.circle!!.id) { openings.findNextOf(it) },
                            loggedIn || request.isInInternalNetwork(),
                            false)
                }
                .collect(Collectors.toList())
        return ResponseEntity(list, HttpStatus.OK)
    }

    data class CircleResponse(val id: Long, val pekId: Long?, val name: String)

    data class OpeningResponse(
        val id: Long,
        val circleId: Long?,
        val feeling: String?,
        val description: String?,
        val start: Long,
        val end: Long,
        val orderingStart: Long,
        val orderingEnd: Long,
        @field:JsonInclude(Include.NON_NULL)
        val outOfStock: Boolean?
    )

    data class SyncResponse(val circles: List<CircleResponse>, val openings: List<OpeningResponse>)


    @Operation(summary = "Endpoint for start.sch with current openings and all circles")
    @GetMapping("/sync")
    @ResponseBody
    fun sync() : SyncResponse = SyncResponse(
        circles = circles.findAll()
            .filter { it.alias != "schami" }
            .map { CircleResponse(it.id, it.virGroupId, it.displayName) },
        openings = openings.findNextWeek().map {
            OpeningResponse(
                id = it.id,
                circleId = it.circle?.id,
                feeling = it.feeling,
                description = it.eventDescription,
                start = it.dateStart,
                end = it.dateEnd,
                orderingStart = it.orderStart,
                orderingEnd = it.orderEnd,
                outOfStock = calculateAvailable(it).coerceAtLeast(0) == 0
            )
        }
    )

    @Operation(summary = "List of ended openings before a given point in time")
    @GetMapping("/openings/ended")
    @ResponseBody
    fun endedOpenings(
        @RequestParam before: Long,
        @RequestParam(defaultValue = "100") count: Long
    ): List<OpeningResponse> =
        openings.findEndedBefore(before.coerceAtLeast(0), count.coerceAtLeast(1))
            .map {
                OpeningResponse(
                    id = it.id,
                    circleId = it.circle?.id,
                    feeling = it.feeling,
                    description = it.eventDescription,
                    start = it.dateStart,
                    end = it.dateEnd,
                    orderingStart = it.orderStart,
                    orderingEnd = it.orderEnd,
                    outOfStock = null
                )
            }

    @Operation(summary = "List of items orderable tomorrow")
    @GetMapping("/items/tomorrow")
    @ResponseBody
    fun getAllItemsTomorrow(request: HttpServletRequest, auth: Authentication?): ResponseEntity<List<ItemEntityDto>> {
        val loggedIn = auth?.isAuthenticated == true || request.isInInternalNetwork()
        val cache: MutableMap<Long, OpeningEntity?> = HashMap()
        val list = items.findAllByOrerableTomorrow().stream()
                .filter { it.visibleWithoutLogin || loggedIn }
                .filter(ItemEntity::visibleInAll)
                .filter(ItemEntity::visible)
                .map { item: ItemEntity ->
                    ItemEntityDto(item,
                            cache.computeIfAbsent(item.circle!!.id) { openings.findNextOf(it) },
                            loggedIn || request.isInInternalNetwork(),
                            false)
                }
                .collect(Collectors.toList())
        return ResponseEntity(list, HttpStatus.OK)
    }

    data class NewOrderRequest(var id: Long = -1,
                               var time: Int = -1,
                               var comment: String = "",
                               var count: Int = 1,
                               var detailsJson: String = "{}",
                               var manualOrderDetails: ManualUserDetails? = null
    )

    @Operation(summary = "New order")
    @PostMapping("/order")
    @ResponseBody
    @Throws(Exception::class)
    fun newOrder(auth: Authentication?, @RequestBody requestBody: NewOrderRequest): ResponseEntity<String> {
        if (requestBody.id < 0 || requestBody.time < 0 || requestBody.detailsJson == "{}")
            return ResponseEntity(RESPONSE_INTERNAL_ERROR, HttpStatus.OK)
        val user = auth.getUserIfPresent() ?: return responseOf("Error 403", HttpStatus.FORBIDDEN)
        return try {
            if (requestBody.manualOrderDetails != null) {
                log.info("{}:{} is making a manual order with details: {}, for {}",
                    user.name, user.uid, requestBody.detailsJson, requestBody.manualOrderDetails?.toString() ?: "null")
                orders.makeManualOrder(user, requestBody.id, requestBody.count, requestBody.time.toLong(),
                    requestBody.comment, requestBody.detailsJson, requestBody.manualOrderDetails!!)
            } else {
                orders.makeOrder(user, requestBody.id, requestBody.count, requestBody.time.toLong(),
                    requestBody.comment, requestBody.detailsJson)
            }
        } catch (e: FailedOrderException) {
            log.warn("Failed to make new order by '${user.uid}' reason: ${e.response}")
            responseOf(e.response)
        }
    }

    data class RoomChangeRequest(var room: String = "")

    @Operation(summary = "Set room code")
    @PostMapping("/user/room")
    @ResponseBody
    fun setRoom(auth: Authentication?, @RequestBody(required = true) requestBody: RoomChangeRequest): String {
        return try {
            users.setRoom(auth.getUserId()!!, requestBody.room)
            "ACK"
        } catch (e: Exception) {
            "REJECT"
        }
    }

    data class DeleteRequestDto(var id: Long = 0)

    @Operation(summary = "Delete order")
    @PostMapping("/order/delete")
    @ResponseBody
    fun deleteOrder(auth: Authentication?, @RequestBody(required = true) body: DeleteRequestDto): ResponseEntity<String> {
        val user = auth.getUserIfPresent() ?: return responseOf("Error 403", HttpStatus.FORBIDDEN)
        return try {
            orders.cancelOrder(user, body.id)
        } catch (e: FailedOrderException) {
            log.warn("Failed to cancel order by '${user.uid}' reason: ${e.response}")
            responseOf(e.response)
        }
    }

    data class ChangeRequestDto(var id: Long = 0, var room: String = "", var comment: String = "")

    @Operation(summary = "Change order")
    @PostMapping("/order/change")
    @ResponseBody
    fun changeOrder(auth: Authentication?, @RequestBody(required = true) body: ChangeRequestDto): ResponseEntity<String> {
        val user = auth.getUserIfPresent() ?: return responseOf("Error 403", HttpStatus.FORBIDDEN)
        return try {
            orders.changeOrder(user, body.id, body.room, body.comment)
        } catch (e: FailedOrderException) {
            log.warn("Failed to change order by '${user.uid}' reason: ${e.response}")
            responseOf(e.response)
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
        return "Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(Instant.now().toEpochMilli())}\n" +
                "Timestamp: ${Instant.now().toEpochMilli()}"
    }

    data class OpeningDetail(
            var name: String,
            var icon: String?,
            var feeling: String,
            var available: Int,
            var outOf: Int,
            var banner: String?,
            var day: String,
            var comment: String,
            var circleUrl: String,
            var circleColor: String
    )

    private val daysOfTheWeek = arrayOf("n/a", "Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat", "Vasárnap")

    @CrossOrigin(origins = ["*"])
    @GetMapping("/open/openings")
    @ResponseBody
    fun openingsApi(@RequestParam(required = false) token: String?): List<OpeningDetail> {
        if (token.isNullOrBlank() || !apiTokens.contains(token))
            return listOf(OpeningDetail("Invalid Token", null, "sad", 0, 0, null,
                    "", "Contact the administrator if you think this is a problem", "", ""))

        val openings = openings.findNextWeek()
            .filter { it.circle != null }
            .filter { it.orderStart + it.compensationTime <= Instant.now().toEpochMilli() }
            .map { openingEntity ->
                OpeningDetail(
                    name = openingEntity.circle?.displayName ?: "n/a",
                    icon = openingEntity.circle?.logoUrl?.let { url -> baseUrl + url },
                    feeling = openingEntity.feeling ?: "",
                    available = calculateAvailable(openingEntity).coerceAtLeast(0),
                    outOf = openingEntity.maxOrder,
                    banner = openingEntity.prUrl.let { url -> baseUrl + url },
                    day = timeService.format(openingEntity.dateStart, "u")?.toInt().let { daysOfTheWeek[it ?: 0] },
                    comment = "${
                        timeService.format(openingEntity.orderEnd, "u")?.toInt().let { daysOfTheWeek[it ?: 0] }
                    } " +
                            "${timeService.format(openingEntity.orderEnd, "HH:mm")}-ig rendelhető",
                    circleUrl = openingEntity.circle?.alias?.let { alias -> baseUrl + "p/" + alias }
                        ?: (baseUrl + "p/" + (openingEntity.circle?.id ?: 0)),
                    circleColor = openingEntity.circle?.cssClassName ?: "none"
                )
            }
            .filter { it.available > 0 }

        return openings.ifEmpty { generateCurrencyInfo() }
    }

    var EUR_PRICE: Int = 0
    var USD_PRICE: Int = 0

    private fun generateCurrencyInfo(): List<OpeningDetail> {
        return if (EUR_PRICE == 0 || USD_PRICE == 0) listOf() else listOf(OpeningDetail(
            name = "EUR / USD",
            icon = "",
            available = EUR_PRICE,
            outOf = USD_PRICE,
            feeling = "JMF",
            comment = "Pár percenként frissül",
            circleColor = "none",
            day = "épp most",
            banner = null,
            circleUrl = ""
        ))
    }

    @Scheduled(fixedRate = 60000 * 5)
    fun updateEurAndUsd() {
        EUR_PRICE = round(getPrice("eur")).toInt()
        USD_PRICE = round(getPrice("usd")).toInt()
        println("EUR: ${EUR_PRICE} USD: ${USD_PRICE}")
    }

    private fun getPrice(currency: String): Double {
        val headers = HttpHeaders()
        headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
        headers.set("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36")

        val body: String? = RestTemplate().exchange<String>("https://www.google.com/search?q=1+${currency}+to+huf", HttpMethod.GET, HttpEntity<String>(headers), String::class).body
        val startIndex = body?.indexOf("data-exchange-rate")
        if (startIndex == null || startIndex < 0)
            return 0.0
        return body.substring(startIndex + 20, body.indexOf("\"", startIndex + 21)).toDoubleOrNull() ?: 0.0
    }

    private fun calculateAvailable(openingEntity: OpeningEntity): Int {
        val orders = orders.findAllByOpening(openingEntity.id)
            .filter { it.status == OrderStatus.ACCEPTED }

        val maxOverall = openingEntity.maxOrder - orders.sumOf { it.count }
        val available = min(maxOverall, orders.groupBy { it.orderedItem?.category ?: 0 }
            .map { pair -> pair.key to pair.value.sumOf { it.count } }
            .maxOfOrNull { pair ->
                when (ItemCategory.of(pair.first)) {
                    ItemCategory.DEFAULT -> maxOverall
                    ItemCategory.ALPHA -> openingEntity.maxAlpha - pair.second
                    ItemCategory.BETA -> openingEntity.maxBeta - pair.second
                    ItemCategory.GAMMA -> openingEntity.maxGamma - pair.second
                    ItemCategory.DELTA -> openingEntity.maxDelta - pair.second
                    ItemCategory.LAMBDA -> openingEntity.maxLambda - pair.second
                }
            } ?: maxOverall)
        return available
    }

}
