package hu.kirdev.schpincer.web;

import hu.kirdev.schpincer.model.*;
import hu.kirdev.schpincer.service.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    CircleService circles;
    
    @Autowired
    OpeningService openings;
    
    @Autowired
    ItemService items;
    
    @Autowired
    ReviewService reviews;
    
    @Autowired
    OrderService orders;

    @Autowired
    ControllerUtil util;
    
    @GetMapping("/")
    public String root(HttpServletRequest request, Map<String, Object> model) {
        List<CircleEntity> circlesList = circles.findAllForMenu();
        model.put("circles", circlesList);
        List<CircleEntity> random = new ArrayList<>(circlesList);
        Collections.shuffle(random);
        model.put("circlesRandom", random);
        List<OpeningEntity> opens = openings.findUpcomingOpenings();
        model.put("opener", opens.size() > 0 ? opens.get(0) : null);
        model.put("openings", openings.findNextWeek());

        UserEntity user = util.getUser(request);
        model.put("orders", Collections.EMPTY_LIST);
        if (user != null) {
            model.put("orders", this.orders.findAll(user.getUid()).stream()
                    .filter(x -> x.getStatus() == OrderStatus.ACCEPTED)
                    .limit(3)
                    .collect(Collectors.toList()));
        }

        return "index";
    }
    
    @GetMapping("/items")
    public String items(@RequestParam(name = "q", defaultValue = "", required = false) String keyword, 
    		Map<String, Object> model) {

        model.put("circles", circles.findAllForMenu());
        model.put("searchMode", !("".equals(keyword)));
        model.put("keyword", keyword);
        return "items";
    }

    @GetMapping("/szor")
    public String circle(Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("circlesWithOpening", circles.findAllForInfo());
        return "circle";
    }
    
    @GetMapping("/circle/{circle}")
    public String circleSpecific(@PathVariable String circle, 
            Map<String, Object> model) {
        
        model.put("circles", circles.findAllForMenu());

        if (circle.matches("^\\d+$")) {
            long id = Long.parseLong(circle);
            model.put("selectedCircle", circles.getOne(id));
            model.put("nextOpening", openings.findNextStartDateOf(id));
        } else {
            CircleEntity circleEntity = circles.findByAlias(circle);
            model.put("selectedCircle", circleEntity);
            model.put("nextOpening", openings.findNextStartDateOf(circleEntity.getId()));
        }
        return "circleProfile";
    }

    @GetMapping({"/provider/{circle}", "/p/{circle}"})
    public String circleSpecificAlias(@PathVariable String circle, Map<String, Object> model) {
        return circleSpecific(circle, model);
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Map<String, Object> model) {
        List<OrderEntity> orders = this.orders.findAll(util.getUser(request).getUid());
        Collections.reverse(orders);

        model.put("orders", orders);
        model.put("circles", circles.findAllForMenu());
        return "profile";
    }
    
}
