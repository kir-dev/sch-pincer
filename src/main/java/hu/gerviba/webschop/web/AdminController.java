package hu.gerviba.webschop.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import hu.gerviba.webschop.service.ItemPrecedenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    ItemPrecedenceService itemSorter;
    
	@GetMapping("/")
	public String adminRoot(Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("circlesToEdit", circles.findAll());
        List<RoleEntryDto> roles = users.findAll().stream()
                .map(user -> {
                    try {
                        return new RoleEntryDto(util.sha256(user.getUid()), user);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(RoleEntryDto::getName))
                .collect(Collectors.toList());
        model.put("roles", roles);
		return "admin";
	}

	@GetMapping("/circles")
	public String adminCircles(Map<String, Object> model) {
		model.put("circles", circles.findAllForMenu());
        model.put("circlesToEdit", circles.findAll());
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
        original.setAlias(circle.getAlias());
        original.setVisible(circle.getVisible());
        
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
        model.put("priority", user.getOrderingPriority());
        return "userModify";
    }

    @PostMapping("/roles/edit/")
    public String editRoles(@RequestParam String uidHash,
            @RequestParam String roles,
            @RequestParam(required = false, defaultValue = "false") boolean sysadmin,
            @RequestParam(defaultValue = "1") Integer priority
    ) {
        
        UserEntity user = users.getByUidHash(uidHash);
        user.setSysadmin(sysadmin);
        user.getPermissions().clear();
        for (String role : roles.split(",")) {
            role = role.trim().toUpperCase();
            if (role.startsWith("CIRCLE_"))
                user.getPermissions().add(role);
            if (role.startsWith("PR_"))
                user.getPermissions().add(role);
        }
        if (user.getPermissions().size() > 0)
            user.getPermissions().add("ROLE_LEADER");

        user.setOrderingPriority(priority);
        users.save(user);

        return "redirect:/admin/";
    }

    @ResponseBody
    @GetMapping("/circles/api/reorder")
    public String reorder() {
        itemSorter.reorder();
        return "ok";
    }

}
