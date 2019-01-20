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

import hu.gerviba.webschop.dao.OpeningEntityDao;
import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.CircleMemberEntity;
import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.CircleMemberService;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;
import hu.gerviba.webschop.service.OpeningService;
import hu.gerviba.webschop.service.OrderService;
import io.swagger.annotations.Api;

@Controller
@Api(value="onlinestore", description="Circle leader pages")
public class ConfigureController {

    @Autowired
    CircleService circles;
    
    @Autowired
    CircleMemberService members;
    
    @Autowired
    ItemService items;

    @Autowired
    OpeningService openings;

    @Autowired
    OrderService orders;
    
    @Autowired
    ControllerUtil util;
    
    @GetMapping("/configure")
    public String configureRoot(HttpServletRequest request, Map<String, Object> model) {
        List<CircleEntity> all = circles.findAllForMenu();
        model.put("circles", all);
        
        UserEntity user = util.getUser(request);
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
            @RequestParam MultipartFile avatarFile,
            HttpServletRequest request) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
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
        model.put("cancel", "configure/" + circleId);
        return "prompt";
    }

    //TODO: hashPermission CHECK IF THE MEMBER IS ON OTHER CIRCLE
    @PostMapping("/configure/{circleId}/members/delete/{memberId}/confirm")
    public String deleteMemberConfirm(@PathVariable Long circleId, @PathVariable Long memberId,
            Map<String, Object> model, HttpServletRequest request) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
        CircleMemberEntity cme = members.getOne(memberId);
        if (cme.getCircle().getId() == circleId)
            members.delete(cme);
        
        return "redirect:/configure/" + circleId;
    }
    
    @GetMapping("/configure/{circleId}/edit")
    public String editCircle(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("mode", "edit");
        model.put("adminMode", false);
        model.put("circle", new CircleEntity(circles.getOne(circleId)));
        return "circleModify";
    }

    @PostMapping("/configure/edit")
    public String editCircle(@Valid CircleEntity circle, 
            @RequestParam Long circleId,
            @RequestParam(required = false) MultipartFile logo,
            @RequestParam(required = false) MultipartFile background,
            HttpServletRequest request) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
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
        model.put("itemId", -1);
        model.put("mode", "new");
        ItemEntity ie = new ItemEntity();
        ie.setDetailsConfigJson("[{\"name\":\"size\",\"values\":[\"1\",\"2\",\"3\"]}]");
        ie.setOrderable(false);
        ie.setVisible(true);
        model.put("item", ie);
        return "itemModify";
    }
    
    @PostMapping("/configure/{circleId}/items/new")
    public String newItem(@PathVariable Long circleId,
            @Valid ItemEntity ie,
            @RequestParam MultipartFile imageFile,
            HttpServletRequest request) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
        CircleEntity circle = circles.getOne(circleId);
        ie.setCircle(circle);
        // TODO: Validate JSON
        
        String file = util.uploadFile("items", imageFile);
        ie.setImageName(file == null ? "image/blank-item.jpg" : "cdn/items/" + file);
        
        items.save(ie);
        return "redirect:/configure/" + circleId;
    }
    
    @GetMapping("/configure/{circleId}/items/edit/{itemId}")
    public String editItem(@PathVariable Long itemId, 
            @PathVariable Long circleId, Map<String, Object> model) {
        
        model.put("circles", circles.findAllForMenu());
        model.put("mode", "edit");
        model.put("item", new ItemEntity(items.getOne(itemId)));
        return "itemModify";
    }

    @PostMapping("/configure/{circleId}/items/edit")
    public String editItem(@PathVariable Long circleId,
            @Valid ItemEntity item, 
            @RequestParam Long itemId,
            @RequestParam(required = false) MultipartFile imageFile,
            HttpServletRequest request) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
        ItemEntity original = items.getOne(itemId);
        
        original.setDescription(item.getDescription());
        original.setDetailsConfigJson(item.getDetailsConfigJson());
        original.setIngredients(item.getIngredients());
        original.setKeywords(item.getKeywords());
        original.setName(item.getName());
        original.setOrderable(item.isOrderable());
        original.setVisible(item.isVisible());
        original.setPrice(item.getPrice());
        
        String file = util.uploadFile("items", imageFile);
        if (file != null)
            original.setImageName("cdn/items/" + file);
        
        items.save(original);
        
        return "redirect:/configure/" + circleId;
    }
    
    @GetMapping("/configure/{circleId}/items/delete/{itemId}")
    public String deleteItem(@PathVariable Long circleId, @PathVariable Long itemId, 
            Map<String, Object> model) {
        
        model.put("circles", circles.findAllForMenu());
        model.put("topic", "member");
        model.put("arg", items.getOne(itemId).getName());
        model.put("ok", "configure/" + circleId + "/items/delete/" + itemId + "/confirm");
        model.put("cancel", "configure/" + circleId);
        return "prompt";
    }

    @PostMapping("/configure/{circleId}/items/delete/{itemId}/confirm")
    public String deleteItemConfirm(@PathVariable Long circleId, @PathVariable Long itemId,
            HttpServletRequest request) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
        ItemEntity ie = items.getOne(itemId);
        if (ie.getCircle().getId() == circleId)
            items.delete(ie);
        
        return "redirect:/configure/" + circleId;
    }
    
    @GetMapping("/configure/{circleId}/openings/new")
    public String newOpening(@PathVariable Long circleId, Map<String, Object> model) {
        model.put("circles", circles.findAllForMenu());
        model.put("circleId", circleId);
        model.put("opening", new OpeningEntityDao());
        return "openingAdd";
    }
    
    @PostMapping("/configure/{circleId}/openings/new")
    public String newOpening(@PathVariable Long circleId,
            OpeningEntityDao oed,
            @RequestParam MultipartFile prFile,
            HttpServletRequest request) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
        OpeningEntity eo = new OpeningEntity();
        eo.setFeeling(oed.getFeeling());
        eo.setCircle(circles.getOne(circleId));
        eo.setDateStart(util.parseDate(oed.getDateStart()));
        eo.setDateEnd(util.parseDate(oed.getDateEnd()));
        eo.setOrderStart(util.parseDate(oed.getOrderStart()));
        eo.setOrderEnd(util.parseDate(oed.getOrderEnd()));
        eo.setTimeIntervals(oed.getTimeIntervals());
        eo.setMaxOrder(oed.getMaxOrder());
        eo.setMaxOrderPerInterval(oed.getMaxOrderPerInterval());
        eo.setIntervalLength(oed.getIntervalLength());
        
        String file = util.uploadFile("pr", prFile);
        eo.setPrUrl(file == null ? "image/blank-pr.jpg" : "cdn/pr/" + file);
        
        openings.save(eo);
        return "redirect:/configure/" + circleId;
    }
    
    @GetMapping("/configure/{circleId}/openings/delete/{openingId}")
    public String deleteOpening(@PathVariable Long circleId, @PathVariable Long openingId, 
            Map<String, Object> model) {
        
        model.put("circles", circles.findAllForMenu());
        model.put("topic", "opening");
        model.put("arg", util.formatDate(openings.getOne(openingId).getDateStart()));
        model.put("ok", "configure/" + circleId + "/openings/delete/" + openingId + "/confirm");
        model.put("cancel", "configure/" + circleId);
        return "prompt";
    }

    @PostMapping("/configure/{circleId}/openings/delete/{openingId}/confirm")
    public String deleteOpeningConfirm(@PathVariable Long circleId, @PathVariable Long openingId,
            HttpServletRequest request) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
        OpeningEntity ie = openings.getOne(openingId);
        if (ie.getCircle().getId() == circleId)
            openings.delete(ie);
        
        return "redirect:/configure/" + circleId;
    }
    
    @GetMapping("/configure/{circleId}/openings/show/{openingId}")
    public String showOpenings(@PathVariable Long circleId, @PathVariable Long openingId, 
            HttpServletRequest request, Map<String, Object> model) {
        
        if (util.cannotEditCircle(circleId, request))
            return "redirect:/configure/" + circleId + "?error";
        
        model.put("circles", circles.findAllForMenu());
        model.put("orders", orders.findAllByOpening(openingId));
        return "openingShow";
    }
}
