package hu.gerviba.webschop.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import hu.gerviba.webschop.dao.ItemEntityDao;
import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;
import hu.gerviba.webschop.service.OpeningService;

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private CircleService circleService;
    
    @Autowired
    private OpeningService openingService;

    @Autowired
    private ItemService items;
    
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
