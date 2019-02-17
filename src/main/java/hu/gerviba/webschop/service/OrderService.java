package hu.gerviba.webschop.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import hu.gerviba.webschop.dao.ItemRepository;
import hu.gerviba.webschop.dao.OpeningRepository;
import hu.gerviba.webschop.dao.OrderRepository;
import hu.gerviba.webschop.dao.TimeWindowRepository;
import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.model.OrderEntity;
import hu.gerviba.webschop.model.OrderStatus;
import hu.gerviba.webschop.model.TimeWindowEntity;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.web.ControllerUtil;
import hu.gerviba.webschop.web.comonent.CustomComponentType;

@Service
@Transactional
public class OrderService {

    @Autowired
    OrderRepository repo;
    
    @Autowired
    OpeningRepository openingRepo;
    
    @Autowired
    ControllerUtil util;
    
    @Autowired
    OpeningService openings;

    @Autowired
    TimeWindowRepository timewindowRepo;

    @Autowired
    ItemRepository itemsRepo;
    
    public void save(OrderEntity order) {
        repo.save(order);
    }

    public List<OrderEntity> findAll(String uid) {
        return repo.findAllByUserIdOrderByDateDesc(uid);
    }
    
    public OrderEntity getOne(Long id) {
        return repo.getOne(id);
    }

    public List<OrderEntity> findAllByOpening(Long openingId) {
        return repo.findAllByOpeningId(openingId);
    }

    public void updateOrder(Long id, OrderStatus os) {
        Optional<OrderEntity> order = repo.findById(id);
        if (order.isPresent()) {
            OrderEntity orderEntity = order.get();
            orderEntity.setStatus(os);
            repo.save(orderEntity);
        }
    }

    public Long getCircleIdByOrderId(Long id) {
        Optional<OrderEntity> order = repo.findById(id);
        if (order.isPresent())
            return openingRepo.getOne(order.get().getOpeningId()).getCircle().getId();
        return null;
    }
    
    public ResponseEntity<String> makeOrder(HttpServletRequest request, Long id, long time, String comment,
            String detailsJson) throws JsonParseException, JsonMappingException, IOException {
        
        UserEntity user = util.getUser(request);
        if (user.getRoom() == null || user.getRoom().length() < 3)
            return new ResponseEntity<String>("NO_ROOM_SET", HttpStatus.OK);
        
        OrderEntity order = new OrderEntity(user.getUid(), user.getName(), comment, detailsJson, user.getRoom());
        order.setIntervalId(time);
        ItemEntity item = itemsRepo.getOne(id);
        order.setName(item.getName());
        CustomComponentType.calculateExtra(detailsJson, order, item);
        order.setOpeningId(openings.findNextOf(item.getCircle().getId()).getId());
        OpeningEntity current = openings.findNextOf(item.getCircle().getId());
        
        if (current == null)
            return new ResponseEntity<String>("INTERNAL_ERROR", HttpStatus.OK);
        
        if (current.getOrderStart() > System.currentTimeMillis() || current.getOrderEnd() < System.currentTimeMillis())
            return new ResponseEntity<String>("NO_ORDERING", HttpStatus.OK);

        if (current.getOrderCount() >= current.getMaxOrder())
            return new ResponseEntity<String>("OVERALL_MAX_REACHED", HttpStatus.OK);
        
        TimeWindowEntity timewindow = timewindowRepo.getOne(time);
        if (timewindow.getOpening().getId() != current.getId())
            return new ResponseEntity<String>("INTERNAL_ERROR", HttpStatus.OK);
        
        if (timewindow.getNormalItemCount() <= 0)
            return new ResponseEntity<String>("MAX_REACHED", HttpStatus.OK);
        
        timewindow.setNormalItemCount(timewindow.getNormalItemCount() - 1);
        current.setOrderCount(current.getOrderCount() + 1);
        
        order.setDate(timewindow.getDate());
        order.setIntervalMessage(timewindow.getName());
        
        timewindowRepo.save(timewindow);
        openings.save(current);
        save(order);
        
        return new ResponseEntity<String>("ACK", HttpStatus.OK);
    }
    
}
