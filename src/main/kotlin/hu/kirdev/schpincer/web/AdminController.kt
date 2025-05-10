package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.dto.RoleEntryDto
import hu.kirdev.schpincer.model.CardType
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.security.core.Authentication

@Controller
@RequestMapping("/admin")
open class AdminController {

    private val REDIRECT_TO_ADMIN = "redirect:/admin/"

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var items: ItemService

    @Autowired
    private lateinit var users: UserService

    @Autowired
    private lateinit var itemSorter: ItemPrecedenceService

    @Autowired
    private lateinit var config: RealtimeConfigService

    @GetMapping("/")
    fun adminRoot(model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circlesToEdit", circles.findAll())
        val roles: List<RoleEntryDto> = users.findAll()
                .map { RoleEntryDto(it.uid.sha256(), it) }
                .sortedBy { it.name }
        model.addAttribute("roles", roles)
        model.addAttribute("configObject", ConfigObject(config))
        config.injectPublicValues(model)
        return "admin"
    }

    @GetMapping("/circles")
    fun adminCircles(model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circlesToEdit", circles.findAll())
        config.injectPublicValues(model)
        return "admin"
    }

    @GetMapping("/circles/new")
    fun adminAddCircle(model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("mode", "new")
        model.addAttribute("adminMode", true)
        model.addAttribute("circle", CircleEntity())
        config.injectPublicValues(model)
        return "circleModify"
    }

    @PostMapping("/circles/new")
    @Throws(IllegalStateException::class, IOException::class)
    fun adminAddCircle(request: HttpServletRequest,
                       circle: @Valid CircleEntity,
                       @RequestParam("logo") logo: MultipartFile?,
                       @RequestParam("background") background: MultipartFile?
    ): String {

        var file = logo?.uploadFile("logos")
        circle.logoUrl = if (file == null) "image/blank-logo.svg" else "cdn/logos/$file"

        file = background?.uploadFile("backgrounds")
        circle.backgroundUrl = if (file == null) "image/blank-background.jpg" else "cdn/backgrounds/$file"

        circles.save(circle)
        return REDIRECT_TO_ADMIN
    }

    @GetMapping("/circles/edit/{circleId}")
    fun adminEditCircle(@PathVariable circleId: Long, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("mode", "edit")
        model.addAttribute("adminMode", true)
        model.addAttribute("circle", circles.getOne(circleId)!!.copy())
        config.injectPublicValues(model)
        return "circleModify"
    }

    @PostMapping("/circles/edit")
    fun adminEditCircle(@ModelAttribute circle:
                        @Valid CircleEntity,
                        bindingResult: BindingResult,
                        @RequestParam circleId: Long,
                        @RequestParam(required = false) logo: MultipartFile?,
                        @RequestParam(required = false) background: MultipartFile?,
                        model: Model
    ): String {

        if (bindingResult.hasErrors()) {
            model.addAttribute("circles", circles.findAllForMenu())
            model.addAttribute("mode", "edit")
            model.addAttribute("adminMode", true)
            model.addAttribute("circle", circle)
            return "circleModify"
        }
        val original = circles.getOne(circleId) ?: return "redirect:/admin/?error=invalidCircleId"

        with(original) {
            avgOpening = circle.avgOpening
            cssClassName = circle.cssClassName
            description = circle.description
            displayName = circle.displayName
            facebookUrl = circle.facebookUrl
            founded = circle.founded
            homePageDescription = circle.homePageDescription
            homePageOrder = circle.homePageOrder
            websiteUrl = circle.websiteUrl
            alias = circle.alias
            visible = circle.visible
            virGroupId = circle.virGroupId

            var file = logo?.uploadFile("logos")
            if (file != null)
                logoUrl = "cdn/logos/$file"

            file = background?.uploadFile("backgrounds")
            if (file != null)
                backgroundUrl = "cdn/backgrounds/$file"
        }
        circles.save(original)
        return REDIRECT_TO_ADMIN
    }

    @GetMapping("/circles/delete/{circleId}")
    fun adminDeleteCircle(@PathVariable circleId: Long, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("topic", "circle")
        model.addAttribute("arg", circles.getOne(circleId)!!.displayName)
        model.addAttribute("ok", "admin/circles/delete/$circleId/confirm")
        model.addAttribute("cancel", "admin/")
        config.injectPublicValues(model)
        return "prompt"
    }

    @PostMapping("/circles/delete/{circleId}/confirm")
    fun adminDeleteCircleConfirm(
            @PathVariable circleId: Long,
            request: HttpServletRequest
    ): String {
        items.deleteByCircle(circleId)
        circles.delete(circles.getOne(circleId)!!)
        return REDIRECT_TO_ADMIN
    }

    @GetMapping("/roles/edit/{uidHash}")
    fun editRoles(@PathVariable uidHash: String, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("uidHash", uidHash)
        val user = users.getByUidHash(uidHash)
        model.addAttribute("name", user!!.name)
        model.addAttribute("sysadmin", user.sysadmin)
        model.addAttribute("email", user.email ?: "-")
        model.addAttribute("roles", user.permissions.joinToString(", "))
        model.addAttribute("priority", user.orderingPriority)
        model.addAttribute("forceGrantLoginAccess", user.forceGrantLoginAccess)
        config.injectPublicValues(model)
        return "userModify"
    }

    @PostMapping("/roles/edit/")
    fun editRoles(@RequestParam uidHash: String,
                  @RequestParam roles: String,
                  @RequestParam(required = false, defaultValue = "false") sysadmin: Boolean,
                  @RequestParam(defaultValue = "1") priority: Int,
                  @RequestParam(required = false, defaultValue = "false") forceGrantLoginAccess: Boolean,
    ): String {
        val user = users.getByUidHash(uidHash) ?: return "redirect:/admin/?error=invalidUidHash"
        user.sysadmin = sysadmin
        val permissions = mutableSetOf<String>()
        for (role in roles.split(",".toRegex()).toTypedArray()) {
            val roleName = role.trim().uppercase(Locale.getDefault())
            if (roleName.startsWith("CIRCLE_"))
                permissions.add(roleName)
            if (roleName.startsWith("PR_"))
                permissions.add(roleName)
        }
        if (permissions.isNotEmpty())
            permissions.add("ROLE_LEADER")
        user.permissions = permissions
        user.orderingPriority = priority
        user.forceGrantLoginAccess = forceGrantLoginAccess
        users.save(user)
        return REDIRECT_TO_ADMIN
    }

    @ResponseBody
    @GetMapping("/circles/api/reorder")
    fun reorder(): String {
        itemSorter.reorder()
        return "ok"
    }

    @ResponseBody
    @GetMapping("/debug/card/{card}")
    fun changeCard(@PathVariable card: String, auth: Authentication?): String {
        val user = auth.getUserIfPresent() ?: return "failed"
        user.cardType = CardType.valueOf(card)
        users.save(user)
        return "ok"
    }

    @PostMapping("/config")
    fun adminConfigUpdate(@ModelAttribute configObject: ConfigObject): String {
        config.messageBoxType = configObject.messageBoxType
        config.messageBoxMessage = configObject.messageBoxMessage
        config.persist()
        return REDIRECT_TO_ADMIN
    }

}
