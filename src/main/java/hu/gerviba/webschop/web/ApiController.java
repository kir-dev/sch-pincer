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
import hu.gerviba.webschop.model.OrderEntity;
import hu.gerviba.webschop.model.OrderStatus;
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
    
    private static final int DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    
    @ApiOperation("Item info")
    @GetMapping("/item/{id}")
    @ResponseBody
    public ItemEntityDto getItem(@PathVariable Long id) {
        ItemEntity item = items.getOne(id);
        return new ItemEntityDto(item, openings.findNextOf(item.getCircle().getId()));
    }

    @ApiOperation("List of items")
    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getAllItems() {
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAll().stream()
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(item.getCircle().getId(), (i) -> openings.findNextOf(i))))
                .collect(Collectors.toList());
        
        return new ResponseEntity<List<ItemEntityDto>>(list, HttpStatus.OK);
    }

    @ApiOperation("List of items orderable today")
    @GetMapping("/items/today")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getAllItemsToday() {
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAllByOrerableNow().stream()
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(item.getCircle().getId(), (i) -> openings.findNextOf(i))))
                .collect(Collectors.toList());
                
        return new ResponseEntity<List<ItemEntityDto>>(list, HttpStatus.OK);
    }

    @ApiOperation("List of items orderable tomorrow")
    @GetMapping("/items/tomorrow")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getAllItemsTomorrow() {
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAllByOrerableTomorrow().stream()
                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(item.getCircle().getId(), (i) -> openings.findNextOf(i))))
                .collect(Collectors.toList());
        
        return new ResponseEntity<List<ItemEntityDto>>(list, HttpStatus.OK);
    }

    @ApiOperation("Page of items")
    @GetMapping("/items/{page}")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> getItems(@PathVariable int page) {
        Map<Long, OpeningEntity> cache = new HashMap<>();
        List<ItemEntityDto> list = items.findAll(page).stream()
                .map(item -> new ItemEntityDto(item, 
                        cache.computeIfAbsent(item.getCircle().getId(), (i) -> openings.findNextOf(i))))
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
    public String setRoom(HttpServletRequest request, @RequestParam(required = true) int room) {
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
    
//    @ApiOperation("Delete order")
//    @PostMapping("/order/delete")
//    @ResponseBody
    public ResponseEntity<String> deleteOrder(HttpServletRequest request, @RequestParam(required = true) long id) {
        try {
            OrderEntity order = orders.getOne(id);
            if (!order.getUserId().equals(util.getUser(request).getUid()))
                return new ResponseEntity<String>("BAD_REQUEST", HttpStatus.BAD_REQUEST);
            order.setStatus(OrderStatus.CANCELLED);
            orders.save(order);
            
            return new ResponseEntity<String>("ACK", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("BAD_REQUEST", HttpStatus.BAD_REQUEST);
        }
    }
    
}
