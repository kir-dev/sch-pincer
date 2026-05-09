package hu.kirdev.schpincer.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.resource.NoResourceFoundException


@ControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseBody
    fun handle404(ex: NoResourceFoundException): ResponseEntity<Void> {
        logger.error("No static resource {} for request '{}'", ex.resourcePath, ex.message)
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

}
