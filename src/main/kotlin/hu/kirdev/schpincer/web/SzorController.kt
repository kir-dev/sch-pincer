package hu.kirdev.schpincer.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.HandlerMapping
import javax.servlet.http.HttpServletRequest

@Controller
open class SzorController {

    @GetMapping(path = ["/americano", "/pizzasch", "/reggelisch", "/kakas", "/langosch", "/vodor", "/dzsajrosz"])
    fun providerRedirects(request: HttpServletRequest): String {
        val providerName = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE) as String
        return "redirect:/p$providerName"
    }

}
