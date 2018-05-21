package hu.gerviba.webschop.web;

import java.util.List;
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

import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.OpeningEntity;
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
    OrderService orders;

    @Autowired
    ControllerUtil util;
    
    @GetMapping("/")
    public String root(Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        List<OpeningEntity> opens = openings.findAll();
        model.put("opener", opens.size() > 0 ? opens.get(0) : null);
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

    @GetMapping("/circle")
    public String circle(Map<String, Object> model) {
        
        model.put("circles", circles.findAllForMenu());
        model.put("openings", openings.findAll()); //TODO: nextWeek
        return "circle";
    }
    
    @GetMapping("/circle/{circleId}")
    public String circleSpecific(@PathVariable Long circleId, 
            Map<String, Object> model) {
        
        
        model.put("circles", circles.findAllForMenu());
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

        
        model.put("circles", circles.findAllForMenu());
        return "circleReview";
    }   
    
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/circle/{circleId}/review")
    public String circleRate(HttpServletRequest request, @PathVariable Long circleId, 
            @ModelAttribute ReviewEntity review, Map<String, Object> model) {
        
        review.setUserName(util.getUser(request).getName());
        review.setDate(System.currentTimeMillis());
        review.setCircle(circles.getOne(circleId));
        reviews.save(review);
        
        List<ReviewEntity> allReviews = reviews.findAll(circleId);
        CircleEntity ce = circles.getOne(circleId);
        ce.setRateingCount(allReviews.size());
        ce.setRateOverAll((float) allReviews.stream().mapToDouble(x -> x.getRateOverAll()).average().orElse(1));
        ce.setRatePrice((float) allReviews.stream().mapToDouble(x -> x.getRatePrice()).average().orElse(1));
        ce.setRateQuality((float) allReviews.stream().mapToDouble(x -> x.getRateQuality()).average().orElse(1));
        ce.setRateSpeed((float) allReviews.stream().mapToDouble(x -> x.getRateSpeed()).average().orElse(1));
        
        circles.save(ce);
        
        
        model.put("circles", circles.findAllForMenu());
        model.put("selectedCircle", ce);
        return "redirect:/circle/" + circleId + "/";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Map<String, Object> model) {
        model.put("orders", orders.findAll(util.getUser(request).getUid()));
        
        model.put("circles", circles.findAllForMenu());
        return "profile";
    }
    
}
