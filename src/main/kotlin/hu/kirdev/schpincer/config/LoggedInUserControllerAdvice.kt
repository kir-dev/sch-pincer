package hu.kirdev.schpincer.config

import hu.kirdev.schpincer.model.SchPincerOidcUser
import hu.kirdev.schpincer.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class LoggedInUserControllerAdvice(private val userService: UserService) {

    @ModelAttribute
    fun addUserToModel(model: Model, auth: Authentication?) {
        val user = auth?.principal as? SchPincerOidcUser ?: return
        val userEntity = userService.getByIdOrNull(user.internalId) ?: return

        model.addAttribute("user", userEntity)
    }

}
