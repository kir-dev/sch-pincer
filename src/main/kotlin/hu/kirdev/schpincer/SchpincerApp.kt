package hu.kirdev.schpincer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication
open class SchpincerApp {

    @PostConstruct
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"))
    }

}

fun main(args: Array<String>) {
    runApplication<SchpincerApp>(*args)
}
