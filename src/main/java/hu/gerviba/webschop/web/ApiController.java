package hu.gerviba.webschop.web;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hu.gerviba.webschop.dao.ItemEntityDao;
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

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private CircleService circles;
    
    @Autowired
    private OpeningService openings;

    @Autowired
    private ItemService items;
    
    @Autowired
    private UserService users;
    
    @Autowired
    private OrderService orders;
    
    @Autowired
    private ControllerUtil util;
    
    private static final long HALF_HOUR = 1000 * 60 * 30;
    private final SimpleDateFormat DATE = new SimpleDateFormat("HH:mm"); 
    
    @GetMapping("/item/{id}")
    @ResponseBody
    public ItemEntityDao getItem(@PathVariable Long id) {
        return new ItemEntityDao(items.getOne(id));
    }
    
    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<List<ItemEntity>> getAllItems() {
        List<ItemEntity> list = items.findAll();
        return new ResponseEntity<List<ItemEntity>>(list, HttpStatus.OK);
    }
    
    @GetMapping("/items/{page}")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDao>> getItems(@PathVariable int page) {
        List<ItemEntityDao> list = items.findAll(page).stream()
                .map(item -> new ItemEntityDao(item))
                .collect(Collectors.toList());
        return new ResponseEntity<List<ItemEntityDao>>(list, HttpStatus.OK);
    }
    
    @GetMapping("/openings")
    @ResponseBody
    public ResponseEntity<List<OpeningEntity>> getAllOpenings() {
        List<OpeningEntity> page = openings.findAll();
        return new ResponseEntity<List<OpeningEntity>>(page, HttpStatus.OK);
    }

    @GetMapping("/openings/week")
    @ResponseBody
    public ResponseEntity<List<OpeningEntity>> getNextWeekOpenings() {
        List<OpeningEntity> page = openings.findNextWeek();
        return new ResponseEntity<List<OpeningEntity>>(page, HttpStatus.OK);
    }
    
    @GetMapping("/circles")
    @ResponseBody
    public ResponseEntity<List<CircleEntity>> getAllCircles() {
        List<CircleEntity> page = circles.findAll();
        return new ResponseEntity<List<CircleEntity>>(page, HttpStatus.OK);
    }

    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<String> newOrder(HttpServletRequest request,
            @RequestParam(required = true) Long id,
            @RequestParam(required = true) int time,
            @RequestParam(required = true) String comment,
            @RequestParam(required = true) String detailsJson) {
        
        UserEntity user = util.getUser(request);
        OrderEntity order = new OrderEntity(user.getUid(), user.getName(), comment, detailsJson, user.getRoom());
        order.setIntervalId(time);
        ItemEntity item = items.getOne(id);
        order.setName(item.getName());
        order.setPrice(item.getPrice());
        order.setOpeningId(openings.findNextOf(item.getCircle().getId()).getId());
        OpeningEntity current = openings.findNextOf(item.getCircle().getId());
        
        long intervalStart = current.getDateStart() + HALF_HOUR * time;
        long intervalEnd = current.getDateStart() + HALF_HOUR * (time + 1);
        order.setDate(intervalStart);
        order.setIntervalMessage(DATE.format(intervalStart) + " - " + DATE.format(intervalEnd));
        
        orders.save(order);
        
        return new ResponseEntity<String>("ACK", HttpStatus.OK);
    }

    @PostMapping("/user/room")
    @ResponseBody
    public String setRoom(HttpServletRequest request, @RequestParam(required = true) int room) {
        try {
            UserEntity user = util.getUser(request);
            user.setRoom(room);
            users.save(user);
            return "ACK"; //new ResponseEntity<String>("ACK", HttpStatus.OK);
        } catch (Exception e) {
            return "REJECT"; //new ResponseEntity<String>("BAD_REQUEST", HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/user/id")
    @ResponseBody
    public String setRoom(HttpServletRequest request) {
        try {
            return util.sha256(util.getUser(request).getUid());
        } catch (Exception e) {
            return "ERROR";
        }
    }
    
    //TODO: DELETE-el nem engedte a jquery
    @PostMapping("/order/delete")
    @ResponseBody
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
