package hu.kirdev.schpincer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
open class SchpincerApp

fun main(args: Array<String>) {
    runApplication<SchpincerApp>(*args)
}
