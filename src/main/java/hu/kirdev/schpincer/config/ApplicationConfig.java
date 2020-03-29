package hu.kirdev.schpincer.config;

import hu.kirdev.schpincer.service.ItemPrecedenceService;
import hu.kirdev.schpincer.web.ControllerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Autowired
    private ItemPrecedenceService itemPrecedenceService;

    @Bean
    public ControllerUtil controllerUtil() {
        return new ControllerUtil();
    }

    @Scheduled(fixedRate = 1000 * 60 * 30, initialDelay = 20000)
    public void reorderItems() {
        log.info("Executing reorder task");
        itemPrecedenceService.reorder();
        log.info("Reorder finished...");
    }

}
