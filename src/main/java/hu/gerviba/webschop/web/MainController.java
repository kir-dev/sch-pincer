package hu.gerviba.webschop.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hu.gerviba.webschop.WebschopUtils;
import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.ReviewEntity;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;
import hu.gerviba.webschop.service.OpeningService;
import hu.gerviba.webschop.service.OrderService;
import hu.gerviba.webschop.service.ReviewService;

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
    private OrderService orders;
    
    @GetMapping("/")
    public String root(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        model.put("opener", openings.findAll().get(0));
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
    public String circleSpecific(@PathVariable Long circleId, 
            Map<String, Object> model) {
        
        model.put("circles", circles.findAll());
        model.put("selectedCircle", circles.getOne(circleId));
        model.put("nextOpening", openings.findNextStartDateOf(circleId));
        return "circleProfile";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/circle/{circleId}/review")
    public String circleSpecificRate(@PathVariable Long circleId, Map<String, Object> model) {
        
        CircleEntity circle = circles.getOne(circleId);
        model.put("selectedCircle", circle);
        model.put("review", new ReviewEntity(circle));

        model.put("circles", circles.findAll());
        return "circleReview";
    }   
    
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/circle/{circleId}/review")
    public String circleRate(HttpServletRequest request, @PathVariable Long circleId, 
            @ModelAttribute ReviewEntity review, Map<String, Object> model) {
        
        review.setUserName(WebschopUtils.getUser(request).getName());
        review.setDate(System.currentTimeMillis());
        review.setCircle(circles.getOne(circleId));
        System.out.println(review.getReview());
        reviews.save(review);
        
        model.put("circles", circles.findAll());
        model.put("selectedCircle", circles.getOne(circleId));
        return "redirect:/circle/" + circleId + "/";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Map<String, Object> model) {
        model.put("orders", orders.findAll(WebschopUtils.getUser(request).getUid()));
        model.put("circles", circles.findAll());
        return "profile";
    }
    
}
