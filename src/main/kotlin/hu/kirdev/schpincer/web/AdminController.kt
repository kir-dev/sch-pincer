package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.config.ApplicationConfig
import hu.kirdev.schpincer.config.Role
import hu.kirdev.schpincer.dao.UserRepository
import hu.kirdev.schpincer.dto.RoleEntryDto
import hu.kirdev.schpincer.model.CardType
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.service.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

const val REDIRECT_TO_ADMIN = "redirect:/admin"


@Controller
@RequestMapping("/admin")
open class AdminController(
    private val circles: CircleService,
    private val items: ItemService,
    private val users: UserService,
    private val itemSorter: ItemPrecedenceService,
    private val config: RealtimeConfigService,
    private val userRepository: UserRepository,
    private val app: ApplicationConfig
) {

    @GetMapping("")
    fun adminRoot(model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("circlesToEdit", circles.findAll())
        val roles: List<RoleEntryDto> = users.findAll()
            .map { RoleEntryDto(it.uid, it) }
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
    fun adminAddCircle(
        request: HttpServletRequest,
        circle: @Valid CircleEntity,
        @RequestParam("logo") logo: MultipartFile?,
        @RequestParam("background") background: MultipartFile?
    ): String {

        var file = logo?.uploadFile(app.uploadPath, "logos")
        circle.logoUrl = if (file == null) "image/blank-logo.svg" else "cdn/logos/$file"

        file = background?.uploadFile(app.uploadPath, "backgrounds")
        circle.backgroundUrl = if (file == null) "image/blank-background.jpg" else "cdn/backgrounds/$file"

        circles.save(circle)
        return REDIRECT_TO_ADMIN
    }

    @GetMapping("/circles/edit/{circleId}")
    fun adminEditCircle(@PathVariable circleId: Long, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("mode", "edit")
        model.addAttribute("adminMode", true)
        model.addAttribute("circle", circles.getOne(circleId)?.copy() ?: return "redirect:/admin/?error=invalidCircleId")
        config.injectPublicValues(model)
        return "circleModify"
    }

    @PostMapping("/circles/edit")
    fun adminEditCircle(
        @ModelAttribute circle:
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

            var file = logo?.uploadFile(app.uploadPath, "logos")
            if (file != null)
                logoUrl = "cdn/logos/$file"

            file = background?.uploadFile(app.uploadPath, "backgrounds")
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
        model.addAttribute("arg", circles.getOne(circleId)?.displayName ?: return "redirect:/admin/?error=invalidCircleId")
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
        val circle = circles.getOne(circleId) ?: return "redirect:/admin/?error=invalidCircleId"
        circles.delete(circle)
        return REDIRECT_TO_ADMIN
    }

    @GetMapping("/roles/edit/{uid}")
    fun editRoles(@PathVariable uid: String, model: Model): String {
        model.addAttribute("circles", circles.findAllForMenu())
        model.addAttribute("uid", uid)
        val user = users.getByIdOrNull(uid) ?: return "redirect:/admin/?error=invalidUid"
        model.addAttribute("name", user.name)
        model.addAttribute("sysadmin", user.sysadmin)
        model.addAttribute("email", user.email ?: "-")
        model.addAttribute("roles", user.permissions.joinToString(", "))
        model.addAttribute("priority", user.orderingPriority)
        model.addAttribute("forceGrantLoginAccess", user.forceGrantLoginAccess)
        model.addAttribute("alwaysGrantAb", user.alwaysGrantAb)
        config.injectPublicValues(model)
        return "userModify"
    }

    @PostMapping("/roles/edit")
    fun editRoles(
        @RequestParam uid: String,
        @RequestParam roles: String,
        @RequestParam(required = false, defaultValue = "false") sysadmin: Boolean,
        @RequestParam(defaultValue = "1") priority: Int,
        @RequestParam(required = false, defaultValue = "false") forceGrantLoginAccess: Boolean,
        @RequestParam(required = false, defaultValue = "false") alwaysGrantAb: Boolean,
    ): String {
        val user = users.getByIdOrNull(uid) ?: return "redirect:/admin/?error=invalidUid"
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
            permissions.add("ROLE_${Role.LEADER.name}")
        user.permissions = permissions
        user.orderingPriority = priority
        user.forceGrantLoginAccess = forceGrantLoginAccess
        user.alwaysGrantAb = alwaysGrantAb
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
        val user = auth.getUser(userRepository) ?: return "failed"
        val cardType = try {
            CardType.valueOf(card)
        } catch (e: IllegalArgumentException) {
            return "failed"
        }
        user.pekCardType = cardType
        users.save(user)
        return "ok"
    }

    @PostMapping("/config")
    fun adminConfigUpdate(@ModelAttribute configObject: ConfigObject): String {
        config.messageBoxType = configObject.messageBoxType
        config.messageBoxMessage = configObject.messageBoxMessage
        return REDIRECT_TO_ADMIN
    }

}
