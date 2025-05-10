package hu.kirdev.schpincer.config

import org.springframework.context.annotation.Configuration
import java.util.*
import jakarta.annotation.PostConstruct

@Configuration
open class TimezoneConfig() {

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Budapest"))
    }

}
