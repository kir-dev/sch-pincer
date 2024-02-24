package hu.kirdev.schpincer.config

import hu.kirdev.schpincer.service.ItemPrecedenceService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import javax.annotation.PostConstruct

@Configuration
@EnableScheduling
open class ApplicationConfig(
    @Value("\${spring.datasource.url:}") private val datasourceUrl: String,
    private val itemPrecedenceService: ItemPrecedenceService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        log.info("datasourceUrl = {}", datasourceUrl)
    }

    @Scheduled(fixedRate = 1000 * 60 * 30L, initialDelay = 20000)
    fun reorderItems() {
        log.info("Executing reorder task")
        itemPrecedenceService.reorder()
        log.info("Reorder finished...")
    }

}
