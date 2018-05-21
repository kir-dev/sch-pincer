package hu.gerviba.webschop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebschopApp {

    public static void main(String[] args) {
        SpringApplication.run(WebschopApp.class, args);
        System.out.println(
                "  _ _ _ _____ _____ _____ _____ _____ _____ _____ \n" + 
                " | | | |   __| __  |   __|     |  |  |     |  _  |\n" + 
                " | | | |   __| __ -|__   |   --|     |  |  |   __|\n" + 
                " |_____|_____|_____|_____|_____|__|__|_____|__|   \n" + 
                "  :: WEBSCHOP ::              Startup completed   \n" + 
                "  Author: Szabo Gergely\n" +
                "  Github: https://github.com/Gerviba/webschop    \n");
    }
    
}
