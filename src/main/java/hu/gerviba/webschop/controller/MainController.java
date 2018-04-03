package hu.gerviba.webschop.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    private String message = "Webschop";

    @RequestMapping("/")
    public String root(Map<String, Object> model) {
        model.put("message", this.message);
        return "index";
    }
    
    @RequestMapping("/items")
    public String items(Map<String, Object> model) {
        model.put("message", this.message);
        return "items";
    }
    
    @RequestMapping("/circle")
    public String circle(Map<String, Object> model) {
        model.put("message", this.message);
        return "circle";
    }
    
    @RequestMapping("/circle/{circleId}")
    public String circleSpecific(Map<String, Object> model) {
        model.put("message", this.message);
        return "circle";
    }

}
