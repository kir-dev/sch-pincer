package hu.kirdev.schpincer.web

import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import hu.gerviba.webschop.dto.OpeningEntityDto
import hu.kirdev.schpincer.model.*
import hu.kirdev.schpincer.service.*
import hu.kirdev.schpincer.web.comonent.ExportType
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

@Controller
class ConfigureController {

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var members: CircleMemberService

    @Autowired
    private lateinit var items: ItemService

    @Autowired
    private lateinit var openings: OpeningService

    @Autowired
    private lateinit var orders: OrderService

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
    fun configure(@PathVariable circleId: Long?, model: Model, request: HttpServletRequest): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circle", circles.getOne(circleId!!))
        model.addAttribute("pr", isPR(circleId, request))
        model.addAttribute("circleId", circleId)
        model.addAttribute("items", items.findAllByCircle(circleId))
        return "configure"
    }

    @GetMapping("/configure/{circleId}/members/new")
    fun newMember(@PathVariable circleId: Long?, model: Model): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("member", CircleMemberEntity())
        return "memberAdd"
    }

    @PostMapping("/configure/{circleId}/members/new")
    fun newMember(@PathVariable circleId: Long,
                  cme: @Valid CircleMemberEntity?,
                  @RequestParam avatarFile: MultipartFile?,
                  request: HttpServletRequest): String? {
        if (cannotEditCircleNoPR(circleId, request)) return "redirect:/configure/$circleId?error"
        val circle = circles.getOne(circleId)
        cme!!.circle = circle
        val file = avatarFile?.uploadFile("avatars", uploadPath)
        cme.avatar = if (file == null) "image/blank-avatar.png" else "cdn/avatars/$file"
        members.save(cme)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/members/delete/{memberId}")
    fun deleteMemberNoPR(@PathVariable circleId: Long,
                         @PathVariable memberId: Long,
                         model: Model): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("topic", "member")
        model.addAttribute("arg", members.getOne(memberId).name)
        model.addAttribute("ok", "configure/$circleId/members/delete/$memberId/confirm")
        model.addAttribute("cancel", "configure/$circleId")
        return "prompt"
    }

    @PostMapping("/configure/{circleId}/members/delete/{memberId}/confirm")
    fun deleteMemberConfirm(@PathVariable circleId: Long,
                            @PathVariable memberId: Long?,
                            request: HttpServletRequest): String? {
        if (cannotEditCircleNoPR(circleId, request)) return "redirect:/configure/$circleId?error"
        val cme = members.getOne(memberId!!)
        if (cme.circle!!.id == circleId) members.delete(cme)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/edit")
    fun editCircle(@PathVariable circleId: Long, model: Model): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("mode", "edit")
        model.addAttribute("adminMode", false)
        model.addAttribute("circle", circles.getOne(circleId)?.copy())
        return "circleModify"
    }

    @PostMapping("/configure/edit")
    fun editCircle(circle: @Valid CircleEntity?,
                   @RequestParam circleId: Long,
                   @RequestParam(required = false) logo: MultipartFile?,
                   @RequestParam(required = false) background: MultipartFile?,
                   request: HttpServletRequest
    ): String? {
        if (cannotEditCircleNoPR(circleId, request)) 
            return "redirect:/configure/$circleId?error=invalidPermissions"
        
        val original = circles.getOne(circleId) ?: return "redirect:/configure/$circleId?error=invalidId"
        original.avgOpening = circle!!.avgOpening
        original.description = circle.description
                .replace("<".toRegex(), "&lt;")
                .replace(">".toRegex(), "&gt;")
        original.displayName = circle.displayName
        original.facebookUrl = circle.facebookUrl
        original.founded = circle.founded
        original.homePageDescription = circle.homePageDescription
        original.websiteUrl = circle.websiteUrl
        
        val logoFile = logo?.uploadFile("logos", uploadPath)
        if (logoFile != null) 
            original.logoUrl = "cdn/logos/$logoFile"
        
        val backgroundFile = background?.uploadFile("backgrounds", uploadPath)
        if (backgroundFile != null) 
            original.backgroundUrl = "cdn/backgrounds/$backgroundFile"
        
        circles.save(original)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/items/new")
    fun newItem(@PathVariable circleId: Long?, model: Model): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("itemId", -1)
        model.addAttribute("mode", "new")
        val ie = ItemEntity(
                detailsConfigJson = "[]",
                orderable = true,
                visible = true,
                service = false,
                visibleInAll = true,
                personallyOrderable = false,
                visibleWithoutLogin = true,
                flag = 0,
                description = "", // TODO: Make them the default value
                imageName = "",
                alias = "",
                name = "",
                ingredients = "")
        model.addAttribute("item", ie)
        return "itemModify"
    }

    @PostMapping("/configure/{circleId}/items/new")
    fun newItem(@PathVariable circleId: Long,
                ie: @Valid ItemEntity,
                @RequestParam imageFile: MultipartFile?,
                request: HttpServletRequest
    ): String? {
        if (cannotEditCircle(circleId, request)) 
            return "redirect:/configure/$circleId?error"
        
        val circle = circles.getOne(circleId)
        ie.circle = circle
        val file: String? = imageFile?.uploadFile("items", uploadPath)
        ie.imageName = if (file == null) "image/blank-item.jpg" else "cdn/items/$file"
        items.save(ie)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/items/edit/{itemId}")
    fun editItem(@PathVariable itemId: Long?,
                 @PathVariable circleId: Long, model: Model,
                 request: HttpServletRequest
    ): String? {
        if (cannotEditCircle(circleId, request)) return "redirect:/configure/$circleId?error"
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("mode", "edit")
        model.addAttribute("item", items.getOne(itemId!!)?.copy())
        return "itemModify"
    }

    @PostMapping("/configure/{circleId}/items/edit")
    fun editItem(@PathVariable circleId: Long,
                 item: @Valid ItemEntity,
                 @RequestParam itemId: Long?,
                 @RequestParam(required = false) imageFile: MultipartFile?,
                 request: HttpServletRequest
    ): String? {
        
        if (cannotEditCircle(circleId, request))
            return "redirect:/configure/$circleId?error=invalidPermissions"

        val original = items.getOne(itemId!!) ?: return "redirect:/configure/$circleId?error=invalidId"

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
        val file: String? = imageFile?.uploadFile("items", uploadPath)
        if (file != null)
            original.imageName = "cdn/items/$file"
        items.save(original)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/items/delete/{itemId}")
    fun deleteItem(@PathVariable circleId: Long, @PathVariable itemId: Long,
                   model: Model): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("topic", "member")
        model.addAttribute("arg", items.getOne(itemId)!!.name)
        model.addAttribute("ok", "configure/$circleId/items/delete/$itemId/confirm")
        model.addAttribute("cancel", "configure/$circleId")
        return "prompt"
    }

    @PostMapping("/configure/{circleId}/items/delete/{itemId}/confirm")
    fun deleteItemConfirm(@PathVariable circleId: Long, @PathVariable itemId: Long?,
                          request: HttpServletRequest): String? {
        if (cannotEditCircle(circleId, request)) return "redirect:/configure/$circleId?error"
        val ie = items.getOne(itemId!!)
        if (ie!!.circle!!.id == circleId) items.delete(ie)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/openings/new")
    fun newOpening(@PathVariable circleId: Long?, model: Model): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circleId", circleId)
        model.addAttribute("opening", OpeningEntityDto())
        return "openingAdd"
    }

    @PostMapping("/configure/{circleId}/openings/new")
    fun newOpening(@PathVariable circleId: Long,
                   oed: OpeningEntityDto,
                   @RequestParam prFile: MultipartFile?,
                   request: HttpServletRequest): String? {
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
        val file = prFile?.uploadFile("pr", uploadPath)
        eo.prUrl = if (file == null) "image/blank-pr.jpg" else "cdn/pr/$file"
        openings.save(eo)
        eo.generateTimeWindows(openings)
        openings.save(eo)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/openings/delete/{openingId}")
    fun deleteOpening(@PathVariable circleId: Long, @PathVariable openingId: Long,
                      model: Model): String? {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("topic", "opening")
        model.addAttribute("arg", formatDate(openings.getOne(openingId).dateStart))
        model.addAttribute("ok", "configure/$circleId/openings/delete/$openingId/confirm")
        model.addAttribute("cancel", "configure/$circleId")
        return "prompt"
    }

    @PostMapping("/configure/{circleId}/openings/delete/{openingId}/confirm")
    fun deleteOpeningConfirm(@PathVariable circleId: Long, @PathVariable openingId: Long?,
                             request: HttpServletRequest): String? {
        if (cannotEditCircle(circleId, request)) 
            return "redirect:/configure/$circleId?error=invalidPermissions"
        val ie = openings.getOne(openingId!!)
        if (ie.circle!!.id == circleId) openings.delete(ie)
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/{circleId}/openings/show/{openingId}")
    fun showOpenings(@PathVariable circleId: Long, @PathVariable openingId: Long?,
                     request: HttpServletRequest, model: Model): String? {
        if (cannotEditCircle(circleId, request)) 
            return "redirect:/configure/$circleId?error=invalidPermissions"
        
        val opening = openings.getOne(openingId!!)
        if (opening.circle!!.id != circleId) 
            return "redirect:/configure/$circleId?error"
        
        model.addAttribute("exportTypes", Arrays.asList(*ExportType.values()))
        model.addAttribute("openingId", opening.id)
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("orders", orders.findAllByOpening(openingId))
        model.addAttribute("opening", opening)
        return "openingShow"
    }

    @PostMapping("/configure/order/update")
    @ResponseBody
    fun updateOrder(@RequestParam id: Long,
                    @RequestParam status: String,
                    request: HttpServletRequest): String? {
        val circleId = orders.getCircleIdByOrderId(id) ?: return "INVALID ID"
        
        if (cannotEditCircle(circleId, request)) 
            return "NO PERMISSION"
        
        orders.updateOrder(id, OrderStatus[status])
        return "redirect:/configure/$circleId"
    }

    @GetMapping("/configure/table-export/{openingId}")
    fun showOpenings(
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
    ): String? {
        val circle = openings.getOne(openingId).circle
        if (cannotEditCircle(circle!!.id!!, request)) return "redirect:/configure/" + circle.id + "?error"
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
                     type: String?,
                     pageOrientation: String?,
                     emptyRows: Int = 0,
                     request: HttpServletRequest
    ): String {
        val opening = openings.getOne(openingId)
        if (cannotEditCircle(opening.circle!!.id!!, request))
            return "redirect:/configure/" + opening.circle!!.id + "?error"

        val document = Document()
        val export = ExportType.valueOf(type!!)

        if (pageOrientation.equals("portrait")) {
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
        if ((emptyRows + orderRows) == 0) { // <-- In case of empty document
            document.add(Chunk(""));
        }

        document.add(table)
        document.close()
        return "redirect:/cdn/export/$name"
    }

    private fun addTableHeader(table: PdfPTable, export: ExportType) {
        table.headerRows = 1
        val font = Font(Font.FontFamily.UNDEFINED, 10.0f)
        export.header.forEach(Consumer { columnTitle ->
            val header = PdfPCell()
            header.horizontalAlignment = Element.ALIGN_CENTER
            header.verticalAlignment = Element.ALIGN_CENTER
            header.phrase = Phrase(columnTitle, font)
            header.borderWidthBottom = 1.5f
            header.fixedHeight = 15.0f
            table.addCell(header)
        })
    }

    private fun addOrderRows(table: PdfPTable, export: ExportType, opening: OpeningEntity): Int {
        val ordersList: List<OrderEntity> = orders.findToExport(opening.id!!, export.orderByFunction)
        for (i in ordersList.indices)
            ordersList[i].artificialTransientId = i + 1

        val font = Font(Font.FontFamily.UNDEFINED, 10.0f)
        for (order in ordersList) {
            for (column in export.fields) {
                val cell = PdfPCell()
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.verticalAlignment = Element.ALIGN_CENTER
                cell.phrase = Phrase(column.apply(order), font)
                cell.isNoWrap = false
                table.addCell(cell)
            }
        }
        return ordersList.size;
    }

    private fun addEmptyRows(table: PdfPTable, export: ExportType, emptyRows: Int) {
        for (i in 1..emptyRows) {
            export.fields.forEach { _ ->
                val cell = PdfPCell()
                cell.phrase = Phrase(" ") // <-- Needs a whitespace
                cell.isNoWrap = false
                table.addCell(cell)
            }
        }
    }

}