package hu.gerviba.webschop;

import hu.gerviba.webschop.service.HibernateSearchService;
import hu.gerviba.webschop.web.ControllerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class ApplicationConfig {

    @Bean
    public ControllerUtil controllerUtil() {
        return new ControllerUtil();
    }

}
