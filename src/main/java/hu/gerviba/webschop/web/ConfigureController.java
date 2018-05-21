package hu.gerviba.webschop.web;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.CircleMemberEntity;
import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.CircleMemberService;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;

@Controller
public class ConfigureController {

    @Autowired
    CircleService circles;
    
    @Autowired
    CircleMemberService members;
    
    @Autowired
    ItemService items;

    @Autowired
    ControllerUtil util;
    
    @GetMapping("/configure")
    public String configureRoot(HttpServletRequest request, Map<String, Object> model) {
        List<CircleEntity> all = circles.findAllForMenu();
        model.put("circles", all);
        
        UserEntity user = util.getUser(request);
        System.out.println("# " + user);
        System.out.println("# " + user.getPermissions());
        System.out.println("# " + user.isSysadmin());
        List<CircleEntity> editable = all.stream()
                .filter(x -> x != null)
                .filter(x -> user.isSysadmin() || user.getPermissions().contains("CIRCLE_" + x.getId()))
                .collect(Collectors.toList());
        model.put("editable", editable);
        return "leaderDashboard";
    }
    
    @GetMapping("/configure/{circleId}")
    public String configure(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("circle", circles.getOne(circleId));
        model.put("circleId", circleId);
        model.put("items", items.findAllByCircle(circleId));
        return "configure";
    }
    
    @GetMapping("/configure/{circleId}/members/new")
    public String newMember(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("circleId", circleId);
        model.put("member", new CircleMemberEntity());
        return "memberAdd";
    }
    
    @PostMapping("/configure/{circleId}/members/new")
    public String newMember(@PathVariable Long circleId,
            @Valid CircleMemberEntity cme,
            @RequestParam MultipartFile avatarFile) {
        
        CircleEntity circle = circles.getOne(circleId);
        cme.setCircle(circle);
        
        String file = util.uploadFile("avatars", avatarFile);
        cme.setAvatar(file == null ? "image/blank-avatar.png" : "cdn/avatars/" + file);
        
        members.save(cme);
        return "redirect:/configure/" + circleId;
    }
    
    //TODO: hashPermission
    @GetMapping("/configure/{circleId}/members/delete/{memberId}")
    public String deleteMember(@PathVariable Long circleId, @PathVariable Long memberId, 
            Map<String, Object> model) {
        
        
        model.put("circles", circles.findAllForMenu());
        model.put("topic", "member");
        model.put("arg", members.getOne(memberId).getName());
        model.put("ok", "configure/" + circleId + "/members/delete/" + memberId + "/confirm");
        model.put("cancel", "admin/");
        return "prompt";
    }

    //TODO: hashPermission CHECK IF THE MEMBER IS ON OTHER CIRCLE
    @PostMapping("/configure/{circleId}/members/delete/{memberId}/confirm")
    public String deleteMemberConfirm(@PathVariable Long circleId, @PathVariable Long memberId,
            Map<String, Object> model) {
        
        CircleMemberEntity cme = members.getOne(memberId);
        if (cme.getCircle().getId() == circleId)
            members.delete(cme);
        
        return "redirect:/configure/" + circleId;
    }
    
    @GetMapping("/configure/{circleId}/edit")
    public String adminEditCircle(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("mode", "edit");
        model.put("adminMode", false);
        model.put("circle", new CircleEntity(circles.getOne(circleId)));
        return "circleModify";
    }

    @PostMapping("/configure/edit")
    public String adminEditCircle(@Valid CircleEntity circle, 
            @RequestParam Long circleId,
            @RequestParam(required = false) MultipartFile logo,
            @RequestParam(required = false) MultipartFile background) {
        
        CircleEntity original = circles.getOne(circleId);
        
        original.setAvgOpening(circle.getAvgOpening());
        original.setDescription(circle.getDescription());
        original.setDisplayName(circle.getDisplayName());
        original.setFacebookUrl(circle.getFacebookUrl());
        original.setFounded(circle.getFounded());
        original.setHomePageDescription(circle.getHomePageDescription());
        original.setWebsiteUrl(circle.getWebsiteUrl());
        
        String file = util.uploadFile("logos", logo);
        if (file != null)
            original.setLogoUrl("cdn/logos/" + file);
        
        file = util.uploadFile("backgrounds", background);
        if (file != null)
            original.setBackgroundUrl("cdn/backgrounds/" + file);
        
        circles.save(original);
        
        return "redirect:/configure/" + circleId;
    }
    
    @GetMapping("/configure/{circleId}/items/new")
    public String newItem(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("circleId", circleId);
        model.put("mode", "new");
        ItemEntity ie = new ItemEntity();
        ie.setDetailsJsonConfig("[{\"name\":\"size\",\"values\":[\"1\",\"2\",\"3\"]}]");
        ie.setOrderable(true);
        model.put("item", ie);
        return "itemModify";
    }
    
    @PostMapping("/configure/{circleId}/items/new")
    public String newItem(@PathVariable Long circleId,
            @Valid ItemEntity ie,
            @RequestParam MultipartFile imageFile) {
        
        CircleEntity circle = circles.getOne(circleId);
        ie.setCircle(circle);
        // TODO: Validate JSON
        
        String file = util.uploadFile("items", imageFile);
        ie.setImageName(file == null ? "image/blank-item.jpg" : "cdn/items/" + file);
        
        items.save(ie);
        return "redirect:/configure/" + circleId;
    }
    
    
}
