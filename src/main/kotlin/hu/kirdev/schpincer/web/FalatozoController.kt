package hu.kirdev.schpincer.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.HandlerMapping
import jakarta.servlet.http.HttpServletRequest

@Controller
open class FalatozoController {

    @GetMapping(path = ["/americano", "/pizzasch", "/reggelisch", "/kakas", "/langosch", "/vodor", "/dzsajrosz", "/schami", "/palacsintazo", "/paschta"])
    fun providerRedirects(request: HttpServletRequest): String {
        val providerName = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) as String
        return "redirect:/p$providerName"
    }

    @GetMapping("/order/{providerId}")
    fun orderProvider(@PathVariable providerId: String): String {
        return "redirect:/provider/$providerId"
    }

}
