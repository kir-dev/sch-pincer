package hu.gerviba.webschop.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import hu.gerviba.webschop.dao.ItemEntityDto;
import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;
import hu.gerviba.webschop.service.OpeningService;
import hu.gerviba.webschop.service.OrderService;
import hu.gerviba.webschop.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
@Api(value="onlinestore", description="RestAPI")
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
        if (util.getUser(request) == null && !item.isVisibleWithoutLogin())
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
                    .filter(item -> item.isVisibleWithoutLogin() || loggedIn)
                    .filter(item -> item.isVisible())
                    .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                            item.getCircle().getId(), 
                            (i) -> openings.findNextOf(i)), 
                            loggedIn || util.isInInternalNetwork(request)))
                    .collect(Collectors.toList());
            return new ResponseEntity<List<ItemEntityDto>>(list, HttpStatus.OK); 
        }
        
        List<ItemEntityDto> list = items.findAll().stream()
                .filter(item -> item.isVisibleWithoutLogin() || loggedIn)
                .filter(item -> item.isVisibleInAll())
                .filter(item -> item.isVisible())
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                        item.getCircle().getId(), 
                        (i) -> openings.findNextOf(i)), 
                        loggedIn || util.isInInternalNetwork(request)))
                .collect(Collectors.toList());
        return new ResponseEntity<List<ItemEntityDto>>(list, HttpStatus.OK);
    }

    @ApiOperation("List of items orderable right now")
    @GetMapping("/items/now")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getAllItemsToday(HttpServletRequest request) {
        boolean loggedIn = util.getUser(request) != null;
        
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAllByOrerableNow().stream()
                .filter(item -> item.isVisibleWithoutLogin() || loggedIn)
                .filter(item -> item.isVisibleInAll())
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                        item.getCircle().getId(), 
                        (i) -> openings.findNextOf(i)), 
                        loggedIn || util.isInInternalNetwork(request)))
                .collect(Collectors.toList());
                
        return new ResponseEntity<List<ItemEntityDto>>(list, HttpStatus.OK);
    }

    @ApiOperation("List of items orderable tomorrow")
    @GetMapping("/items/tomorrow")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getAllItemsTomorrow(HttpServletRequest request) {
        boolean loggedIn = util.getUser(request) != null;
        
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAllByOrerableTomorrow().stream()
                .filter(item -> item.isVisibleWithoutLogin() || loggedIn)
                .filter(item -> item.isVisibleInAll())
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
                        item.getCircle().getId(), 
                        (i) -> openings.findNextOf(i)), 
                        loggedIn || util.isInInternalNetwork(request)))
                .collect(Collectors.toList());
        
        return new ResponseEntity<List<ItemEntityDto>>(list, HttpStatus.OK);
    }

    @ApiOperation("Page of items")
    @GetMapping("/items/{page}")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getItems(HttpServletRequest request, @PathVariable int page) {
        boolean loggedIn = util.getUser(request) != null;
        
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAll(page).stream()
                .filter(item -> item.isVisibleWithoutLogin() || loggedIn)
                .filter(item -> item.isVisibleInAll())
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
        return new ResponseEntity<List<OpeningEntity>>(page, HttpStatus.OK);
    }

    @ApiOperation("List of openings (next week period)")
    @GetMapping("/openings/week")
    @ResponseBody
    public ResponseEntity<List<OpeningEntity>> getNextWeekOpenings() {
        List<OpeningEntity> page = openings.findNextWeek();
        return new ResponseEntity<List<OpeningEntity>>(page, HttpStatus.OK);
    }

    @ApiOperation("List of circles")
    @GetMapping("/circles")
    @ResponseBody
    public ResponseEntity<List<CircleEntity>> getAllCircles() {
        List<CircleEntity> page = circles.findAll();
        return new ResponseEntity<List<CircleEntity>>(page, HttpStatus.OK);
    }

    @ApiOperation("New order")
    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<String> newOrder(HttpServletRequest request,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) int time,
            @RequestParam(required = true) String comment,
            @RequestParam(required = true) String detailsJson) throws Exception {
        
        return orders.makeOrder(request, id, time, comment, detailsJson);
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
    
    @ApiOperation("Delete order")
    @PostMapping("/order/delete")
    @ResponseBody
    public ResponseEntity<String> deleteOrder(HttpServletRequest request, @RequestParam(required = true) long id) {
        return orders.cancelOrder(request, id);
    }

    
}
