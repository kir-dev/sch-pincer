package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.model.OpeningEntity
import hu.kirdev.schpincer.service.AcceleratorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
class AcceleratorController {

    @Autowired
    lateinit var acceleratorService: AcceleratorService

    @PostMapping("/accelerate/{provider}")
    @ResponseBody
    fun addAcceleration(request: HttpServletRequest, @PathVariable provider: String, model: Model): Boolean {
        acceleratorService.accelerateCircle(provider, request.getUser())
        return true
    }

    @GetMapping("/accelerate/{provider}/today")
    @ResponseBody
    fun getAccelerationsToday(@PathVariable provider: String, model: Model): Number =
        acceleratorService.getAcceleratedInOpening(provider, System.currentTimeMillis() - 86400000, System.currentTimeMillis())

}