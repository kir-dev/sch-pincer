package hu.kirdev.schpincer.web;

import hu.gerviba.webschop.dto.ItemEntityDto;
import hu.kirdev.schpincer.model.CircleEntity;
import hu.kirdev.schpincer.model.ItemEntity;
import hu.kirdev.schpincer.model.OpeningEntity;
import hu.kirdev.schpincer.model.UserEntity;
import hu.kirdev.schpincer.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    CircleService circles;
    
    @Autowired
    OpeningService openings;

    @Autowired
    ItemService items;
    
    @Autowired
    UserService users;
    
    @Autowired
    OrderService orders;
    
    @Autowired
    ControllerUtil util;
    
    @ApiOperation("Item info")
    @GetMapping("/item/{id}")
    @ResponseBody
    public ItemEntityDto getItem(HttpServletRequest request, @PathVariable Long id) {
        ItemEntity item = items.getOne(id);
        if (util.getUser(request) == null && !item.getVisibleWithoutLogin())
            return null;
        
        boolean loggedIn = util.getUser(request) != null;
        return new ItemEntityDto(item, openings.findNextOf(item.getCircle().getId()), loggedIn);
    }

    @ApiOperation("List of items")
    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getAllItems(
            HttpServletRequest request, 
            @RequestParam(required = false) Long circle) {
        
        Map<Long, OpeningEntity> cache = new HashMap<>();
        boolean loggedIn = util.getUser(request) != null;
        
        if (circle != null) {
            List<ItemEntityDto> list = items.findAllByCircle(circle).stream()
                    .filter(item -> item.getVisibleWithoutLogin() || loggedIn)
                    .filter(ItemEntity::getVisible)
                    .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                            item.getCircle().getId(), 
                            (i) -> openings.findNextOf(i)), 
                            loggedIn || util.isInInternalNetwork(request)))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(list, HttpStatus.OK);
        }
        
        List<ItemEntityDto> list = items.findAll().stream()
                .filter(item -> item.getVisibleWithoutLogin() || loggedIn)
                .filter(ItemEntity::getVisibleInAll)
                .filter(ItemEntity::getVisible)
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                        item.getCircle().getId(), 
                        (i) -> openings.findNextOf(i)), 
                        loggedIn || util.isInInternalNetwork(request)))
                .collect(Collectors.toList());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation("List of items orderable right now")
    @GetMapping("/items/now")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getAllItemsToday(HttpServletRequest request) {
        boolean loggedIn = util.getUser(request) != null;
        
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAllByOrderableNow().stream()
                .filter(item -> item.getVisibleWithoutLogin() || loggedIn)
                .filter(ItemEntity::getVisibleInAll)
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                        item.getCircle().getId(), 
                        (i) -> openings.findNextOf(i)), 
                        loggedIn || util.isInInternalNetwork(request)))
                .collect(Collectors.toList());
                
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation("List of items orderable tomorrow")
    @GetMapping("/items/tomorrow")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getAllItemsTomorrow(HttpServletRequest request) {
        boolean loggedIn = util.getUser(request) != null;
        
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAllByOrerableTomorrow().stream()
                .filter(item -> item.getVisibleWithoutLogin() || loggedIn)
                .filter(ItemEntity::getVisibleInAll)
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                        item.getCircle().getId(), 
                        (i) -> openings.findNextOf(i)), 
                        loggedIn || util.isInInternalNetwork(request)))
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @ApiOperation("Page of items")
    @GetMapping("/items/{page}")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getItems(HttpServletRequest request, @PathVariable int page) {
        boolean loggedIn = util.getUser(request) != null;
        
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAll(page).stream()
                .filter(item -> item.getVisibleWithoutLogin() || loggedIn)
                .filter(ItemEntity::getVisibleInAll)
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                        item.getCircle().getId(), 
                        (i) -> openings.findNextOf(i)), 
                        loggedIn || util.isInInternalNetwork(request)))
                .collect(Collectors.toList());
        return new ResponseEntity<List<ItemEntityDto>>(list, HttpStatus.OK);
    }

    @ApiOperation("List of openings")
    @GetMapping("/openings")
    @ResponseBody
    public ResponseEntity<List<OpeningEntity>> getAllOpenings() {
        List<OpeningEntity> page = openings.findAll();
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @ApiOperation("List of openings (next week period)")
    @GetMapping("/openings/week")
    @ResponseBody
    public ResponseEntity<List<OpeningEntity>> getNextWeekOpenings() {
        List<OpeningEntity> page = openings.findNextWeek();
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @ApiOperation("List of circles")
    @GetMapping("/circles")
    @ResponseBody
    public ResponseEntity<List<CircleEntity>> getAllCircles() {
        List<CircleEntity> page = circles.findAll();
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @ApiOperation("New order")
    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<String> newOrder(HttpServletRequest request,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) int time,
            @RequestParam(required = true) String comment,
            @RequestParam(defaultValue = "1") int count,
            @RequestParam(required = true) String detailsJson) throws Exception {

        return orders.makeOrder(request, id, count, time, comment, detailsJson);
    }

    @ApiOperation("Set room code")
    @PostMapping("/user/room")
    @ResponseBody
    public String setRoom(HttpServletRequest request, @RequestParam(required = true) String room) {
        try {
            UserEntity user = util.getUser(request);
            user.setRoom(room);
            users.save(user);
            return "ACK";
        } catch (Exception e) {
            return "REJECT";
        }
    }

    @ApiOperation("Hased user id")
    @GetMapping("/user/id")
    @ResponseBody
    public String setRoom(HttpServletRequest request) {
        try {
            return util.sha256(util.getUser(request).getUid());
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @Deprecated
    @ApiOperation("Delete order")
    @PostMapping("/order/delete")
    @ResponseBody
    public ResponseEntity<String> deleteOrder(HttpServletRequest request, @RequestParam(required = true) long id) {
        return orders.cancelOrder(request, id);
    }

    @GetMapping("/version")
    @ResponseBody
    public String version() {
        return "Version: " + getClass().getPackage().getImplementationVersion();
    }

    @GetMapping("/time")
    @ResponseBody
    public String time() {
        return "Time: "
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(System.currentTimeMillis())
                + " \nTimestamp: " + System.currentTimeMillis();
    }

}