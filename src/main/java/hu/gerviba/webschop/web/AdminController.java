package hu.gerviba.webschop.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import hu.gerviba.webschop.service.CircleService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    CircleService circles;
    
	@GetMapping("/")
	public String adminRoot(Map<String, Object> model) {
		return "admin";
	}

	@GetMapping("/circles")
	public String adminCircles(Map<String, Object> model) {
		model.put("circles", circles.findAll());
		return "admin";
	}
	
}
