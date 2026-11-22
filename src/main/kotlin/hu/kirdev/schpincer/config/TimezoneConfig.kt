package hu.kirdev.schpincer.config

import org.springframework.context.annotation.Configuration
import java.time.ZoneId
import java.util.*
import jakarta.annotation.PostConstruct

/** The time zone the whole application works in. */
val APP_ZONE: ZoneId = ZoneId.of("Europe/Budapest")

@Configuration
open class TimezoneConfig() {

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone(APP_ZONE))
    }

}
