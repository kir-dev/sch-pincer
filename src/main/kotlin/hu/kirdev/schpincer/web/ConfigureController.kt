package hu.kirdev.schpincer.web

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import hu.kirdev.schpincer.dto.CircleMemberRole
import hu.kirdev.schpincer.dto.OpeningEntityDto
import hu.kirdev.schpincer.model.*
import hu.kirdev.schpincer.service.*
import hu.kirdev.schpincer.web.component.ExportType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

enum class PageTypes(val orientation: String) {
    PORTRAIT("portrait"),
    LANDSCAPE("landscape")
}

data class OrderUpdateDto(val id: Long, val status: String)

@Controller
open class ConfigureController {

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var members: CircleMemberService

    @Autowired
    private lateinit var items: ItemService

    @Autowired
    private lateinit var users: UserService

    @Autowired
    private lateinit var openings: OpeningService

    @Autowired
    private lateinit var orders: OrderService

    @Autowired
    private lateinit var reviews: ReviewService

    @Autowired
    private lateinit var timeService: TimeService

    @Value("\${schpincer.external:/etc/schpincer/external}")
    private lateinit var uploadPath: String

    @GetMapping("/configure")
    fun configureRoot(request: HttpServletRequest, model: Model): String? {
        val all = circles.findAllForMenu()
        model.addAttribute("circles", all)
        val (_, _, _, _, sysadmin, _, permissions) = request.getUser()
        val editable = circles.findAll().stream()
                .filter { obj: CircleEntity? -> Objects.nonNull(obj) }
                .filter { x: CircleEntity -> sysadmin || permissions.contains("CIRCLE_" + x.id) }
                .collect(Collectors.toList())
        model.addAttribute("editable", editable)
        return "leaderDashboard"
    }

    @GetMapping("/configure/{circleId}")
    fun configure(@PathVariable circleId: Long, model: Model, request: HttpServletRequest): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circle", circles.getOne(circleId))
        model.addAttribute("pr", isPR(circleId, request))
        model.addAttribute("owner", isCircleOwner(circleId, request))
        model.addAttribute("roles", users.findAllCircleRole(circleId).filter { it.permission !== CircleMemberRole.NONE })
        model.addAttribute("circleId", circleId)
        model.addAttribute("items", items.findAllByCircle(circleId))
        model.addAttribute("timeService", timeService)
        return "configure"
    }

    @GetMapping("/configure/{circleId}/roles/list")
    fun listUserRole(@PathVariable circleId: Long, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("roles", users.findAllCircleRole(circleId))
        return "circleRoleList"
    }

    @GetMapping("/configure/{circleId}/roles/edit/{uidHash}")
    fun editUserRole(@PathVariable circleId: Long, @PathVariable uidHash: String, model: Model): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("role", users.findPermissionByUidHash(uidHash, circleId))
        return "circleRoleModify"
    }

    @PostMapping("/configure/{circleId}/roles/edit")
    fun editUserRoles(@PathVariable circleId: Long,
                      @RequestParam uidHash: String,
                      @RequestParam permission: CircleMemberRole,
                      request: HttpServletRequest
    ): String {
        val user = users.getByUidHash(uidHash) ?: return "redirect:/configure/${circleId}?error=invalidUidHash"
        if (!isCircleOwner(circleId, request) || user.sysadmin) return "redirect:/configure/$circleId?error"

        val tmp: MutableSet<String> = user.permissions.toMutableSet()
        when (permission) {
            CircleMemberRole.NONE -> tmp.removeAll(listOf("CIRCLE_${circleId}", "PR_${circleId}"))
            CircleMemberRole.PR -> tmp.addAll(listOf("CIRCLE_${circleId}", "PR_${circleId}", "ROLE_LEADER"))
            CircleMemberRole.LEADER -> {
                tmp.addAll(listOf("CIRCLE_${circleId}", "ROLE_LEADER"))
                tmp.remove("PR_${circleId}")
            }
        }
        if (tmp.count() == 1 && tmp.first() == "ROLE_LEADER") tmp.clear()
        user.permissions = tmp
        users.save(user)

        return "redirect:/configure/$circleId/roles/list"
    }

    @GetMapping("/configure/{circleId}/members/new")
    fun newMember(@PathVariable circleId: Long, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("member", CircleMemberEntity())
        model.addAttribute("mode", "new")
        return "memberModify"
    }

    @PostMapping("/configure/{circleId}/members/new")
    fun newMember(@PathVariable circleId: Long,
                  cme: @Valid CircleMemberEntity?,
                  @RequestParam avatarFile: MultipartFile?,
                  request: HttpServletRequest): String {
        if (cannotEditCircleNoPR(circleId, request)) return "redirect:/configure/$circleId?error"
        val circle = circles.getOne(circleId)
        cme!!.circle = circle
        val file = avatarFile?.uploadFile("avatars")
        cme.avatar = if (file == null) "image/blank-avatar.png" else "cdn/avatars/$file"
        members.save(cme)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/members/edit/{memberId}")
    fun editMember(@PathVariable circleId: Long,
                   @PathVariable memberId: Long,
                   model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("mode", "edit")
        model.addAttribute("member", members.getOne(memberId))
        return "memberModify"
    }

    @PostMapping("/configure/{circleId}/members/edit")
    fun editMember(@PathVariable circleId: Long,
                   cme: @Valid CircleMemberEntity,
                   @RequestParam id: Long,
                   @RequestParam avatarFile: MultipartFile?,
                   request: HttpServletRequest
    ): String {
        if (cannotEditCircleNoPR(circleId, request)) return "redirect:/configure/$circleId?error"

        val original = members.getOne(id)
        if (original.circle!!.id != circleId) return "redirect:/configure/$circleId?error"

        original.name = cme.name
        original.precedence = cme.precedence
        original.rank = cme.rank
        original.sort = cme.sort
        val file = avatarFile?.uploadFile("avatars")
        if (file != null) original.avatar = "cdn/avatars/$file"

        members.save(original)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/members/delete/{memberId}")
    fun deleteMemberNoPR(@PathVariable circleId: Long,
                         @PathVariable memberId: Long,
                         model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("topic", "member")
        model.addAttribute("arg", members.getOne(memberId).name)
        model.addAttribute("ok", "configure/$circleId/members/delete/$memberId/confirm")
        model.addAttribute("cancel", "configure/$circleId")
        return "prompt"
    }

    @PostMapping("/configure/{circleId}/members/delete/{memberId}/confirm")
    fun deleteMemberConfirm(@PathVariable circleId: Long,
                            @PathVariable memberId: Long,
                            request: HttpServletRequest): String {
        if (cannotEditCircleNoPR(circleId, request)) return "redirect:/configure/$circleId?error"
        val cme = members.getOne(memberId)
        if (cme.circle!!.id == circleId) members.delete(cme)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/edit")
    fun editCircle(@PathVariable circleId: Long, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("mode", "edit")
        model.addAttribute("adminMode", false)
        model.addAttribute("circle", circles.getOne(circleId)?.copy())
        return "circleModify"
    }

    @PostMapping("/configure/edit")
    fun editCircle(circle: @Valid CircleEntity,
                   @RequestParam circleId: Long,
                   @RequestParam(required = false) logo: MultipartFile?,
                   @RequestParam(required = false) background: MultipartFile?,
                   request: HttpServletRequest
    ): String {
        if (cannotEditCircleNoPR(circleId, request))
            return "redirect:/configure/$circleId?error=invalidPermissions"

        val original = circles.getOne(circleId) ?: return "redirect:/configure/$circleId?error=invalidId"
        original.avgOpening = circle.avgOpening
        original.description = circle.description
                .replace("<".toRegex(), "&lt;")
                .replace(">".toRegex(), "&gt;")
        original.displayName = circle.displayName
        original.facebookUrl = circle.facebookUrl
        original.founded = circle.founded
        original.homePageDescription = circle.homePageDescription
        original.websiteUrl = circle.websiteUrl

        val logoFile = logo?.uploadFile("logos")
        if (logoFile != null)
            original.logoUrl = "cdn/logos/$logoFile"

        val backgroundFile = background?.uploadFile("backgrounds")
        if (backgroundFile != null)
            original.backgroundUrl = "cdn/backgrounds/$backgroundFile"

        circles.save(original)
        return "redirect:/configure/$circleId"
    }

    @PostMapping("/configure/{circleId}/mass-update-items")
    fun massUpdateItems(
            @RequestParam allRequestParams: Map<String,String>,
            @PathVariable circleId: Long,
            request: HttpServletRequest
    ): String {
        if (cannotEditCircleNoPR(circleId, request))
            return "redirect:/configure/$circleId?error=invalidPermissions"

        if (!allRequestParams.containsKey("action"))
            return "redirect:/configure/$circleId?error=invalidRequest"

        val itemEntities = items.findAllByCircle(circleId)
                .filter { allRequestParams.containsKey("item${it.id}") }
                .filter { allRequestParams["item${it.id}"] != "off" }

        when (allRequestParams["action"]) {
            "visible" -> { // TODO: extract them
                itemEntities.forEach {
                    it.visible = true
                    it.visibleInAll = true
                }
            }
            "not-visible" -> {
                itemEntities.forEach {
                    it.visible = false
                    it.visibleInAll = false
                }
            }
            "orderable" -> {
                itemEntities.forEach { it.orderable = true }
            }
            "not-orderable" -> {
                itemEntities.forEach { it.orderable = false }
            }
            "price" -> {
                val price = allRequestParams["arg"]?.toIntOrNull() ?: return "redirect:/configure/$circleId?error=invalidPrice"
                itemEntities.forEach { it.price = price }
            }
            "json" -> {
                val json = allRequestParams["arg"] ?: return "redirect:/configure/$circleId?error=invalidJson"
                itemEntities.forEach { it.detailsConfigJson = json }
            }
            else -> return "redirect:/configure/$circleId?error=invalidAction"
        }
        items.saveAll(itemEntities)

        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/items/new")
    fun newItem(@PathVariable circleId: Long, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("itemId", -1)
        model.addAttribute("mode", "new")
        val ie = ItemEntity(
                orderable = true,
                visible = true,
                service = false,
                visibleInAll = true,
                personallyOrderable = false,
                visibleWithoutLogin = true)
        model.addAttribute("item", ie)
        return "itemModify"
    }

    @PostMapping("/configure/{circleId}/items/new")
    fun newItem(@PathVariable circleId: Long,
                ie: @Valid ItemEntity,
                @RequestParam imageFile: MultipartFile?,
                request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request))
            return "redirect:/configure/$circleId?error"

        val circle = circles.getOne(circleId)
        ie.circle = circle
        val file: String? = imageFile?.uploadFile("items")
        ie.imageName = if (file == null) "image/blank-item.jpg" else "cdn/items/$file"
        items.save(ie)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/items/edit/{itemId}")
    fun editItem(@PathVariable itemId: Long,
                 @PathVariable circleId: Long, model: Model,
                 request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request))
            return "redirect:/configure/$circleId?error"

        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("mode", "edit")
        model.addAttribute("item", items.getOne(itemId)?.copy())
        return "itemModify"
    }

    @PostMapping("/configure/{circleId}/items/edit")
    fun editItem(@PathVariable circleId: Long,
                 item: @Valid ItemEntity,
                 @RequestParam itemId: Long,
                 @RequestParam(required = false) imageFile: MultipartFile?,
                 request: HttpServletRequest
    ): String {

        if (cannotEditCircle(circleId, request))
            return "redirect:/configure/$circleId?error=invalidPermissions"

        val original = items.getOne(itemId) ?: return "redirect:/configure/$circleId?error=invalidId"

        with(original) {
            description = item.description
                    .replace("<".toRegex(), "&lt;")
                    .replace(">".toRegex(), "&gt;")
            detailsConfigJson = item.detailsConfigJson
            ingredients = item.ingredients
            keywords = item.keywords
            name = item.name
            orderable = item.orderable
            visible = item.visible
            price = item.price
            personallyOrderable = item.personallyOrderable
            service = item.service
            visibleInAll = item.visibleInAll
            visibleWithoutLogin = item.visibleWithoutLogin
            alias = item.alias
            manualPrecedence = item.manualPrecedence
            discountPrice = item.discountPrice
            category = item.category
        }
        if (item.flag < 1000 || request.getUser().sysadmin)
            original.flag = item.flag
        val file = imageFile?.uploadFile("items")
        if (file != null)
            original.imageName = "cdn/items/$file"
        items.save(original)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/items/delete/{itemId}")
    fun deleteItem(@PathVariable circleId: Long,
                   @PathVariable itemId: Long,
                   model: Model
    ): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("topic", "item")
        model.addAttribute("arg", items.getOne(itemId)!!.name)
        model.addAttribute("ok", "configure/$circleId/items/delete/$itemId/confirm")
        model.addAttribute("cancel", "configure/$circleId")
        return "prompt"
    }

    @PostMapping("/configure/{circleId}/items/delete/{itemId}/confirm")
    fun deleteItemConfirm(@PathVariable circleId: Long,
                          @PathVariable itemId: Long,
                          request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request)) return "redirect:/configure/$circleId?error"
        val ie = items.getOne(itemId)
        if (ie!!.circle!!.id == circleId) items.delete(ie)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/openings/new")
    fun newOpening(@PathVariable circleId: Long, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("opening", OpeningEntityDto())
        return "openingAdd"
    }

    @PostMapping("/configure/{circleId}/openings/new")
    fun newOpening(@PathVariable circleId: Long,
                   oed: OpeningEntityDto,
                   @RequestParam prFile: MultipartFile?,
                   request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request)) return "redirect:/configure/$circleId?error"
        val eo = OpeningEntity(
                feeling = oed.feeling,
                circle = circles.getOne(circleId),
                dateStart = parseDate(oed.dateStart),
                dateEnd = parseDate(oed.dateEnd),
                orderStart = parseDate(oed.orderStart),
                orderEnd = parseDate(oed.orderEnd),
                timeIntervals = oed.timeIntervals,
                maxOrder = oed.maxOrder,
                maxOrderPerInterval = oed.maxOrderPerInterval,
                maxExtraPerInterval = oed.maxExtraPerInterval,
                intervalLength = oed.intervalLength,
                maxAlpha = oed.maxAlpha,
                maxBeta = oed.maxBeta,
                maxGamma = oed.maxGamma,
                maxDelta = oed.maxDelta,
                maxLambda = oed.maxLambda
        )
        val file = prFile?.uploadFile("pr")
        eo.prUrl = if (file == null) "image/blank-pr.jpg" else "cdn/pr/$file"
        openings.save(eo)
        eo.generateTimeWindows(openings)
        openings.save(eo)
        return "redirect:/configure/$circleId"
    }

    @PostMapping("/configure/{circleId}/openings/{openingId}/edit")
    fun editOpening(@PathVariable circleId: Long,
                    @PathVariable openingId: Long,
                    oed: OpeningEntityDto,
                    request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request)) return "redirect:/configure/$circleId?error"
        val opening = openings.getOne(openingId)
        with(opening) {
            feeling = oed.feeling
            if (oed.orderStart != "") orderStart = parseDate(oed.orderStart)
            if (oed.orderEnd != "") orderEnd = parseDate(oed.orderEnd)
            maxOrder = oed.maxOrder
            maxAlpha = oed.maxAlpha
            maxBeta = oed.maxBeta
            maxGamma = oed.maxGamma
            maxDelta = oed.maxDelta
            maxLambda = oed.maxLambda
        }
        openings.save(opening)
        return "redirect:/configure/$circleId"
    }

    @PostMapping("/configure/{circleId}/action/{openingId}/close-all")
    fun applyCloseAllAction(@PathVariable circleId: Long,
                             @PathVariable openingId: Long,
                             request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/configure/$circleId?error"

        orders.closeAllOrdersInOpening(openingId)
        return "redirect:/configure/$circleId"
    }

    @PostMapping("/configure/{circleId}/action/{openingId}/cancel-all")
    fun applyCancelAllAction(@PathVariable circleId: Long,
                            @PathVariable openingId: Long,
                            request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/configure/$circleId?error"

        orders.cancelAllOrdersInOpening(openingId)
        return "redirect:/configure/$circleId"
    }

    @ResponseBody
    @PostMapping("/configure/{circleId}/action/{openingId}/emails")
    fun applyCollectEmails(@PathVariable circleId: Long,
                             @PathVariable openingId: Long,
                             request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId, circleId))
            return "redirect:/configure/$circleId?error"

        return orders.findAllByOpening(openingId)
                .map { users.getById(it.userId) }
                .distinctBy { it.uid }
                .map { "\"${it.name}\" &lt;${it.email ?: ""}&gt;" }
                .joinToString(", <br>")
    }

    @GetMapping("/configure/{circleId}/openings/delete/{openingId}")
    fun deleteOpening(@PathVariable circleId: Long,
                      @PathVariable openingId: Long,
                      model: Model
    ): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("topic", "opening")
        model.addAttribute("arg", formatDate(openings.getOne(openingId).dateStart))
        model.addAttribute("ok", "configure/$circleId/openings/delete/$openingId/confirm")
        model.addAttribute("cancel", "configure/$circleId")
        return "prompt"
    }

    @PostMapping("/configure/{circleId}/openings/delete/{openingId}/confirm")
    fun deleteOpeningConfirm(@PathVariable circleId: Long,
                             @PathVariable openingId: Long?,
                             request: HttpServletRequest
    ): String {
        if (cannotEditCircle(circleId, request) || !openings.isCircleMatches(openingId ?: 0, circleId))
            return "redirect:/configure/$circleId?error=invalidPermissions"
        val ie = openings.getOne(openingId!!)
        orders.cancelAllOrdersInOpening(openingId)
        if (ie.circle!!.id == circleId)
            openings.delete(ie)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/openings/show/{openingId}")
    fun showOpenings(@PathVariable circleId: Long,
                     @PathVariable openingId: Long,
                     request: HttpServletRequest,
                     model: Model
    ): String {
        if (cannotEditCircle(circleId, request))
            return "redirect:/configure/$circleId?error=invalidPermissions"

        val opening = openings.getOne(openingId)
        if (opening.circle!!.id != circleId)
            return "redirect:/configure/$circleId?error"

        model.addAttribute("exportTypes", ExportType.values())
        model.addAttribute("openingId", opening.id)
        model.addAttribute("circles", circles.findAllForMenu())
        val ordersToReturn = orders.findAllByOpening(openingId)
        model.addAttribute("orders", ordersToReturn)
        model.addAttribute("opening", opening)
        model.addAttribute("timeService", timeService)
        model.addAttribute("notCancelledCount", ordersToReturn.filter { it.status != OrderStatus.CANCELLED }.count())
        return "openingShow"
    }

    @PostMapping("/configure/order/update")
    @ResponseBody
    fun updateOrder(@RequestBody body: OrderUpdateDto,
                    request: HttpServletRequest
    ): String {
        val circleId = orders.getCircleIdByOrderId(body.id) ?: return "INVALID ID"

        if (cannotEditCircle(circleId, request))
            return "NO PERMISSION"

        orders.updateOrder(body.id, OrderStatus[body.status])
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/reviews")
    fun showReviews(@PathVariable circleId: Long, model: Model, request: HttpServletRequest): String? {
        val reviewList = reviews.findAll(circleId)
        model.addAttribute("reviews", reviewList)
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circle", circles.getOne(circleId))
        model.addAttribute("reviewCount", reviewList.size)
        model.addAttribute("avgQuality", if (reviewList.isNotEmpty()) "%.2f".format(reviewList.sumBy { it.rateQuality } / reviewList.size.toFloat()) else null)
        model.addAttribute("avgPrice", if (reviewList.isNotEmpty()) "%.2f".format(reviewList.sumBy { it.ratePrice } / reviewList.size.toFloat()) else null)
        model.addAttribute("avgSpeed", if (reviewList.isNotEmpty()) "%.2f".format(reviewList.sumBy { it.rateSpeed } / reviewList.size.toFloat()) else null)
        model.addAttribute("avgOverAll", if (reviewList.isNotEmpty()) "%.2f".format(reviewList.sumBy { it.rateOverAll } / reviewList.size.toFloat()) else null)
        model.addAttribute("timeService", timeService)
        return "circleReviews"
    }

    @GetMapping("/configure/table-export/{openingId}")
    fun exportOpening(
            @PathVariable openingId: Long,
            @RequestParam orderby: String,
            @RequestParam fields: String,
            @RequestParam(defaultValue = "off") artificialId: String,
            @RequestParam(defaultValue = "off") userName: String,
            @RequestParam(defaultValue = "off") name: String,
            @RequestParam(defaultValue = "off") count: String,
            @RequestParam(defaultValue = "off") compactName: String,
            @RequestParam(defaultValue = "off") intervalId: String,
            @RequestParam(defaultValue = "off") comment: String,
            @RequestParam(defaultValue = "off") room: String,
            @RequestParam(defaultValue = "off") extra: String,
            @RequestParam(defaultValue = "off") extraTag: String,
            @RequestParam(defaultValue = "off") price: String,
            @RequestParam(defaultValue = "off") status: String,
            @RequestParam(defaultValue = "off") category: String,
            @RequestParam(defaultValue = "off") systemComment: String,
            request: HttpServletRequest,
            model: Model
    ): String {
        val circle = openings.getOne(openingId).circle
        if (cannotEditCircle(circle!!.id, request))
            return "redirect:/configure/${circle.id}?error"

        model.addAttribute("artificialId", artificialId != "off")
        model.addAttribute("userName", userName != "off")
        model.addAttribute("name", name != "off")
        model.addAttribute("count", count != "off")
        model.addAttribute("compactName", compactName != "off")
        model.addAttribute("intervalId", intervalId != "off")
        model.addAttribute("comment", comment != "off")
        model.addAttribute("room", room != "off")
        model.addAttribute("extra", extra != "off")
        model.addAttribute("extraTag", extraTag != "off")
        model.addAttribute("price", price != "off")
        model.addAttribute("status", status != "off")
        model.addAttribute("category", category != "off")
        model.addAttribute("systemComment", systemComment != "off")
        model.addAttribute("fields", fields)
        model.addAttribute("orders", orders.findToExport(openingId, orderby))
        return "exportTable"
    }

    @GetMapping("/configure/export")
    @Throws(FileNotFoundException::class, DocumentException::class)
    fun showOpenings(openingId: Long,
                     type: String,
                     pageOrientation: String,
                     @RequestParam(defaultValue = "0") emptyRows: Int = 0,
                     request: HttpServletRequest
    ): String {
        val opening = openings.getOne(openingId)
        if (cannotEditCircle(opening.circle!!.id, request))
            return "redirect:/configure/" + opening.circle!!.id + "?error"

        val document = Document()
        val export = ExportType.valueOf(type)

        if (pageOrientation.equals(PageTypes.PORTRAIT.orientation)) {
            document.pageSize = PageSize.A4
        } else {
            document.pageSize = PageSize.A4.rotate()
        }

        val path = "$uploadPath/export/".replace("//", "/")
        File(path).mkdirs()

        val name = opening.circle!!.alias + "_" + export.name + "_" + openingId + ".pdf"
        PdfWriter.getInstance(document, FileOutputStream(path + name))
        document.open()
        document.setMargins(20.0f, 20.0f, 30.0f, 30.0f)
        document.newPage()

        val table = PdfPTable(export.header.size)
        table.setWidths(export.widths)
        table.widthPercentage = 100f
        addTableHeader(table, export)
        val orderRows = addOrderRows(table, export, opening)
        addEmptyRows(table, export, emptyRows)
        isDocumentEmpty(emptyRows, orderRows, document)

        document.add(table)
        document.close()
        return "redirect:/cdn/export/$name"
    }

    private fun isDocumentEmpty(emptyRows: Int, orderRows: Int, document: Document) {
        if ((emptyRows + orderRows) == 0) {
            document.add(Chunk(""))
        }
    }

    private fun addTableHeader(table: PdfPTable, export: ExportType) {
        table.headerRows = 1
        val font = Font(Font.FontFamily.UNDEFINED, export.fontSize)
        export.header.forEach({ columnTitle ->
            val header = PdfPCell()
            header.horizontalAlignment = Element.ALIGN_CENTER
            header.verticalAlignment = Element.ALIGN_CENTER
            header.phrase = Phrase(columnTitle, font)
            header.borderWidthBottom = 1.5f
            header.fixedHeight = 15f * (export.fontSize / 10f)
            table.addCell(header)
        })
    }

    private fun addOrderRows(table: PdfPTable, export: ExportType, opening: OpeningEntity): Int {
        val ordersList: List<OrderEntity> = orders.findToExport(opening.id, export.orderByFunction)
        var accumulator = 1
        for (i in ordersList.indices) {
            ordersList[i].artificialTransientId = accumulator
            accumulator += if (export.denormalizeOrders) ordersList[i].count else 1
        }

        val font = Font(Font.FontFamily.UNDEFINED, export.fontSize)
        for (order in ordersList) {
            if (export.denormalizeOrders) {
                for (id in 0 until order.count) {
                    for (column in export.fields) {
                        val cell = PdfPCell()
                        cell.horizontalAlignment = Element.ALIGN_CENTER
                        cell.verticalAlignment = Element.ALIGN_CENTER
                        cell.phrase = Phrase(column(order), font)
                        cell.isNoWrap = false
                        table.addCell(cell)
                    }
                    order.artificialTransientId++
                }
            } else {
                for (column in export.fields) {
                    val cell = PdfPCell()
                    cell.horizontalAlignment = Element.ALIGN_CENTER
                    cell.verticalAlignment = Element.ALIGN_CENTER
                    cell.phrase = Phrase(column(order), font)
                    cell.isNoWrap = false
                    table.addCell(cell)
                }
            }
        }
        return ordersList.size
    }

    private fun addEmptyRows(table: PdfPTable, export: ExportType, emptyRows: Int) {
        for (i in 1..emptyRows) {
            export.fields.forEach { _ ->
                val cell = PdfPCell()
                cell.phrase = Phrase(" ") // NOTE: Empty phrases are ignored. Therefore at least
                                                // one whitespace character is needed to be added.
                cell.fixedHeight = 15f * (export.fontSize / 10f)
                cell.isNoWrap = false
                table.addCell(cell)
            }
        }
    }

}