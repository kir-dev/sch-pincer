package hu.kirdev.schpincer.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.annotation.PostConstruct

@Configuration
open class TimezoneConfig(
        @Value("\${schpincer.summertime:true}") val summertime: Boolean
) {

    @PostConstruct
    fun init() {
        if (summertime)
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+2:00"))
        else
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"))
    }

}