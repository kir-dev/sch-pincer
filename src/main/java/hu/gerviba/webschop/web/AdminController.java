package hu.gerviba.webschop.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.service.CircleService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    CircleService circles;
    
    @Value("${webschop.external}")
    private String uploadPath = "/etc/webschop/external/";
    
	@GetMapping("/")
	public String adminRoot(Map<String, Object> model) {
        model.put("circles", circles.findAll());
		return "admin";
	}

	@GetMapping("/circles")
	public String adminCircles(Map<String, Object> model) {
		model.put("circles", circles.findAll());
		return "admin";
	}

    @GetMapping("/circles/new")
    public String adminAddCircle(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        model.put("mode", "new");
        model.put("circle", new CircleEntity());
        return "circleModify";
    }

    @PostMapping("/circles/new")
    public String adminAddCircle(HttpServletRequest request, CircleEntity circle, 
            @RequestParam("logo") MultipartFile logo
//            , @RequestParam("file") MultipartFile background
            ) throws IllegalStateException, IOException {
        File dir = new File(uploadPath, "logos");
        dir.mkdirs();
        
        String fileName = "logos/" + new UUID(System.currentTimeMillis(), new Random().nextLong()).toString();
        fileName += logo.getOriginalFilename().substring(logo.getOriginalFilename().lastIndexOf('.'));
        logo.transferTo(new File(uploadPath, fileName));
        
        return "redirect:/admin/";
    }
    
    @GetMapping("/circles/edit")
    public String adminEditCircle(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        model.put("mode", "edit");
        model.put("circle", new CircleEntity());
        return "circleModify";
    }

    @GetMapping("/circles/delete")
    public String adminDeleteCircle(Map<String, Object> model) {
        model.put("circles", circles.findAll());
        return "admin";
    }
    
}
