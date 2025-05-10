package hu.kirdev.schpincer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication()
open class SchpincerApp

fun main(args: Array<String>) {
    runApplication<SchpincerApp>(*args)
}
