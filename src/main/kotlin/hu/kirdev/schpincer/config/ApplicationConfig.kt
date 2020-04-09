package hu.kirdev.schpincer.config

import hu.kirdev.schpincer.service.ItemPrecedenceService
import hu.kirdev.schpincer.web.ControllerUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
open class ApplicationConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private val itemPrecedenceService: ItemPrecedenceService? = null

    @Bean
    open fun controllerUtil(): ControllerUtil {
        return ControllerUtil()
    }

    @Scheduled(fixedRate = 1000 * 60 * 30, initialDelay = 20000)
    fun reorderItems() {
        log.info("Executing reorder task")
        itemPrecedenceService!!.reorder()
        log.info("Reorder finished...")
    }

}
