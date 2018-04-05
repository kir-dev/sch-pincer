package hu.gerviba.webschop.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.OpeningService;

@Controller
public class MainController {

    @Autowired
    CircleService circles;
    
    @Autowired
    OpeningService openings;
    
    @RequestMapping("/")
    public String root(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        model.put("opener", openings.findAll().get(0));
        return "index";
    }
    
    @RequestMapping("/items")
    public String items(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        return "items";
    }
    
    @RequestMapping("/circle")
    public String circle(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        model.put("openings", openings.findAll()); //TODO: nextWeek
        return "circle";
    }
    
    @RequestMapping("/circle/{circleId}")
    public String circleSpecific(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        return "circle";
    }

}
