package hu.gerviba.webschop.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;
import hu.gerviba.webschop.service.OpeningService;

@Controller
public class MainController {

    @Autowired
    CircleService circles;
    
    @Autowired
    OpeningService openings;
    
    @Autowired
    ItemService items;
    
    @GetMapping("/")
    public String root(HttpServletRequest request, Map<String, Object> model) {
        model.put("circles", circles.findAll());
        model.put("opener", openings.findAll().get(0));
        model.put("user", getUser(request));
        return "index";
    }
    
    @GetMapping("/items")
    public String items(@RequestParam(name = "q", defaultValue = "", required = false) String keyword, 
    		Map<String, Object> model) {

    	model.put("circles", circles.findAll());
        model.put("searchMode", !("".equals(keyword)));
        model.put("keyword", keyword);
        return "items";
    }

    @GetMapping("/circle")
    public String circle(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        model.put("openings", openings.findAll()); //TODO: nextWeek
        return "circle";
    }
    
    @GetMapping("/circle/{circleId}")
    public String circleSpecific(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAll());
        model.put("selectedCircle", circles.getOne(circleId));
        return "circle_profile";
    }

    UserEntity getUser(HttpServletRequest request) {
    	return (UserEntity) request.getSession().getAttribute("user");
    }
    
}
