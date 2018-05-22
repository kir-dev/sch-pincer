package hu.gerviba.webschop;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hu.gerviba.webschop.web.ControllerUtil;

@Configuration
public class ApplicationConfig {
    
    @Bean
    public ControllerUtil controllerUtil() {
        return new ControllerUtil();
    }
    
}
