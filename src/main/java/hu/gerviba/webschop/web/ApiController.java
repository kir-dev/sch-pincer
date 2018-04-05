package hu.gerviba.webschop.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.OpeningService;

@Controller
@RequestMapping("/api")
public class ApiController {

    // GET /api/items
    // GET /api/items/{skipCount}
    // GET /api/item/{itemId}
    // GET /api/orders/{circleId}
    
    @Autowired
    private CircleService circleService;
    
    @Autowired
    private OpeningService openingService;
    
    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<List<CircleEntity>> getAllAuctionItems() {
        List<CircleEntity> page = circleService.findAll();
        return new ResponseEntity<List<CircleEntity>>(page, HttpStatus.OK);
    }
    
    @GetMapping("/openings")
    @ResponseBody
    public ResponseEntity<List<OpeningEntity>> getAllOpenings() {
        List<OpeningEntity> page = openingService.findAll();
        return new ResponseEntity<List<OpeningEntity>>(page, HttpStatus.OK);
    }

    @GetMapping("/openings/week")
    @ResponseBody
    public ResponseEntity<List<OpeningEntity>> getNextWeekOpenings() {
        List<OpeningEntity> page = openingService.findNextWeek();
        return new ResponseEntity<List<OpeningEntity>>(page, HttpStatus.OK);
    }
    
}
