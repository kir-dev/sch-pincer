package hu.kirdev.schpincer.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SzorController {

    @GetMapping({"/americano", "/pizzasch", "/reggelisch", "/kakas", "/langosch", "/vodor", "/dzsajrosz"})
    public String providerRedirects(HttpServletRequest request) {
        String providerName = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return "redirect:/p" + providerName;
    }

}
