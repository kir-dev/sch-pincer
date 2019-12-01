package hu.gerviba.webschop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class WebschopApp {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1:00"));
    }

    public static void main(String[] args) {
        SpringApplication.run(WebschopApp.class, args);
    }
    
}
