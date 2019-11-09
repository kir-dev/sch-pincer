package hu.gerviba.webschop.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import hu.gerviba.webschop.model.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.model.ReviewEntity;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;
import hu.gerviba.webschop.service.OpeningService;
import hu.gerviba.webschop.service.OrderService;
import hu.gerviba.webschop.service.ReviewService;
import io.swagger.annotations.Api;

@Controller
@Api(value="onlinestore", description="Pages for Users and Guests")
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
    public String root(Map<String, Object> model) {
        List<CircleEntity> circlesList = circles.findAllForMenu();
        model.put("circles", circlesList);
        List<CircleEntity> random = new ArrayList<>(circlesList);
        Collections.shuffle(random);
        model.put("circlesRandom", random);
        List<OpeningEntity> opens = openings.findUpcomingOpenings();
        model.put("opener", opens.size() > 0 ? opens.get(0) : null);
        model.put("openings", openings.findNextWeek());
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
        model.put("openings", openings.findNextWeek());
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

//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/circle/{circleId}/review")
    public String circleSpecificRate(@PathVariable Long circleId, Map<String, Object> model) {
        
        CircleEntity circle = circles.getOne(circleId);
        model.put("selectedCircle", circle);
        model.put("review", new ReviewEntity(circle));
        model.put("circles", circles.findAllForMenu());
        return "circleReview";
    }   
    
//    @PreAuthorize("hasRole('USER')")
//    @PostMapping("/circle/{circleId}/review")
    public String circleRate(HttpServletRequest request, @PathVariable Long circleId, 
            @ModelAttribute ReviewEntity review, Map<String, Object> model) {
        
        review.setUserName(util.getUser(request).getName());
        review.setDate(System.currentTimeMillis());
        review.setCircle(circles.getOne(circleId));
        reviews.save(review);
        
        List<ReviewEntity> allReviews = reviews.findAll(circleId);
        CircleEntity ce = circles.getOne(circleId);
        ce.setRateingCount(allReviews.size());
        ce.setRateOverAll((float) allReviews.stream().mapToDouble(ReviewEntity::getRateOverAll).average().orElse(1));
        ce.setRatePrice((float) allReviews.stream().mapToDouble(ReviewEntity::getRatePrice).average().orElse(1));
        ce.setRateQuality((float) allReviews.stream().mapToDouble(ReviewEntity::getRateQuality).average().orElse(1));
        ce.setRateSpeed((float) allReviews.stream().mapToDouble(ReviewEntity::getRateSpeed).average().orElse(1));
        
        circles.save(ce);
        
        model.put("circles", circles.findAllForMenu());
        model.put("selectedCircle", ce);
        return "redirect:/provider/" + circleId + "/";
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
