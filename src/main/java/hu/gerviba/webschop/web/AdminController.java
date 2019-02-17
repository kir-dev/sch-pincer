package hu.gerviba.webschop.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import hu.gerviba.webschop.dao.RoleEntryDto;
import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;
import hu.gerviba.webschop.service.UserService;
import io.swagger.annotations.Api;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
@Api(value="onlinestore", description="Admin pages")
public class AdminController {

    @Autowired
    CircleService circles;

    @Autowired
    ItemService items;

    @Autowired
    UserService users;
    
    @Autowired
    ControllerUtil util;
    
	@GetMapping("/")
	public String adminRoot(Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        List<RoleEntryDto> roles = users.findAll().stream()
                .map(x -> {
                    try {
                        return new RoleEntryDto(util.sha256(x.getUid()), x);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(x -> x != null)
                .collect(Collectors.toList());
        model.put("roles", roles);
		return "admin";
	}

	@GetMapping("/circles")
	public String adminCircles(Map<String, Object> model) {
		model.put("circles", circles.findAllForMenu());
		return "admin";
	}

    @GetMapping("/circles/new")
    public String adminAddCircle(Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("mode", "new");
        model.put("adminMode", true);
        model.put("circle", new CircleEntity());
        return "circleModify";
    }

    @PostMapping("/circles/new")
    public String adminAddCircle(HttpServletRequest request, 
            @Valid CircleEntity circle, 
            @RequestParam("logo") MultipartFile logo, 
            @RequestParam("background") MultipartFile background) throws IllegalStateException, IOException {
        
        circle.setRateingCount(0);
        circle.setRateOverAll(0);
        circle.setRatePrice(0);
        circle.setRateQuality(0);
        circle.setRateSpeed(0);
        
        String file = util.uploadFile("logos", logo);
        circle.setLogoUrl(file == null ? "image/blank-logo.svg" : "cdn/logos/" + file);
        
        file = util.uploadFile("backgrounds", background);
        circle.setBackgroundUrl(file == null ? "image/blank-background.jpg" : "cdn/backgrounds/" + file);
        
        circles.save(circle);
        
        return "redirect:/admin/";
    }
    
    @GetMapping("/circles/edit/{circleId}")
    public String adminEditCircle(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("mode", "edit");
        model.put("adminMode", true);
        model.put("circle", new CircleEntity(circles.getOne(circleId)));
        return "circleModify";
    }

    @PostMapping("/circles/edit")
    public String adminEditCircle(@ModelAttribute @Valid CircleEntity circle, 
    		BindingResult bindingResult,
            @RequestParam Long circleId,
            @RequestParam(required = false) MultipartFile logo,
            @RequestParam(required = false) MultipartFile background,
            Map<String, Object> model) {
        
        if (bindingResult.hasErrors()) {
            model.put("circles", circles.findAllForMenu());
            model.put("mode", "edit");
            model.put("adminMode", true);
            model.put("circle", circle);
            return "circleModify";
        }
        
        CircleEntity original = circles.getOne(circleId);
        
        original.setAvgOpening(circle.getAvgOpening());
        original.setCssClassName(circle.getCssClassName());
        original.setDescription(circle.getDescription());
        original.setDisplayName(circle.getDisplayName());
        original.setFacebookUrl(circle.getFacebookUrl());
        original.setFounded(circle.getFounded());
        original.setHomePageDescription(circle.getHomePageDescription());
        original.setHomePageOrder(circle.getHomePageOrder());
        original.setWebsiteUrl(circle.getWebsiteUrl());
        
        String file = util.uploadFile("logos", logo);
        if (file != null)
            original.setLogoUrl("cdn/logos/" + file);
        
        file = util.uploadFile("backgrounds", background);
        if (file != null)
            original.setBackgroundUrl("cdn/backgrounds/" + file);
        
        circles.save(original);
        
        return "redirect:/admin/";
    }
    
    @GetMapping("/circles/delete/{circleId}")
    public String adminDeleteCircle(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("topic", "circle");
        model.put("arg", circles.getOne(circleId).getDisplayName());
        model.put("ok", "admin/circles/delete/" + circleId + "/confirm");
        model.put("cancel", "admin/");
        return "prompt";
    }

    @PostMapping("/circles/delete/{circleId}/confirm")
    public String adminDeleteCircleConfirm(@PathVariable Long circleId, 
            HttpServletRequest request) {
        
//        if (!util.getUser(request).isSysadmin())
//            return "redirect:/admin/?error";
        
        items.deleteByCircle(circleId);
        circles.delete(circles.getOne(circleId));
        return "redirect:/admin/";
    }
    
    @GetMapping("/roles/edit/{uidHash}")
    public String editRoles(@PathVariable String uidHash, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("uidHash", uidHash);
        UserEntity user = users.getByUidHash(uidHash);
        model.put("name", user.getName());
        model.put("sysadmin", user.isSysadmin());
        model.put("roles", String.join(", ", user.getPermissions()));
        return "userModify";
    }

    @PostMapping("/roles/edit/")
    public String editRoles(@RequestParam String uidHash,
            @RequestParam String roles,
            @RequestParam(required = false, defaultValue = "false") boolean sysadmin) {
        
        UserEntity user = users.getByUidHash(uidHash);
        user.setSysadmin(sysadmin);
        user.getPermissions().clear();
        for (String role : roles.split(",")) {
            role = role.trim().toUpperCase();
            if (role.startsWith("CIRCLE_"))
                user.getPermissions().add(role);
        }
        if (user.getPermissions().size() > 0)
            user.getPermissions().add("ROLE_LEADER");
        
        users.save(user);
        
        return "redirect:/admin/";
    }
    
}
