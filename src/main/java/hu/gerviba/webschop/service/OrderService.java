package hu.gerviba.webschop.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import hu.gerviba.webschop.dao.*;
import hu.gerviba.webschop.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.web.ControllerUtil;
import hu.gerviba.webschop.web.comonent.CustomComponentType;

import static java.util.stream.Collectors.groupingBy;

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

    public static final String ORDER_ABSOLUTE = "absolute";
    public static final String ORDER_GROUPED = "grouped";
    public static final String ORDER_ROOMS = "rooms";

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
        return order.map(orderEntity -> openingRepo.getOne(orderEntity.getOpeningId()).getCircle().getId()).orElse(null);
    }
    
    public ResponseEntity<String> makeOrder(HttpServletRequest request, Long id, int count, long time, String comment,
            String detailsJson) throws IOException {
        
        UserEntity user = util.getUser(request);
        if (user == null)
            return new ResponseEntity<>("Error 403", HttpStatus.FORBIDDEN);
        
        if (user.getRoom() == null || user.getRoom().isEmpty())
            return new ResponseEntity<>("NO_ROOM_SET", HttpStatus.OK);
        
        OrderEntity order = new OrderEntity(user.getUid(), user.getName(),
                "[" + user.getCardType().name() + "] " + comment,
                detailsJson, user.getRoom());
        order.setIntervalId(time);
        ItemEntity item = itemsRepo.getOne(id);
        
        if (!item.isOrderable() || item.isPersonallyOrderable())
            return new ResponseEntity<>("INTERNAL_ERROR", HttpStatus.OK);
        
        order.setName(item.getName());
        OrderDetails details = CustomComponentType.calculateExtra(detailsJson, order, item);
        order.setOpeningId(openings.findNextOf(item.getCircle().getId()).getId());
        OpeningEntity current = openings.findNextOf(item.getCircle().getId());
        
        if (current == null)
            return new ResponseEntity<>("INTERNAL_ERROR", HttpStatus.OK);
        
        if (current.getOrderStart() > System.currentTimeMillis() || current.getOrderEnd() < System.currentTimeMillis())
            return new ResponseEntity<>("NO_ORDERING", HttpStatus.OK);

        if (count < details.getMinCount())
            count = details.getMinCount();
        else if (count > details.getMaxCount())
            count = details.getMaxCount();

        if (current.getOrderCount() + count > current.getMaxOrder())
            return new ResponseEntity<>("OVERALL_MAX_REACHED", HttpStatus.OK);

        TimeWindowEntity timewindow = timewindowRepo.getOne(time);
        if (!timewindow.getOpening().getId().equals(current.getId()))
            return new ResponseEntity<>("INTERNAL_ERROR", HttpStatus.OK);

        if (timewindow.getNormalItemCount() - count < 0)
            return new ResponseEntity<>("MAX_REACHED", HttpStatus.OK);

        if (order.isExtraTag() && timewindow.getExtraItemCount() - count < 0) {
            return new ResponseEntity<>("MAX_REACHED_EXTRA", HttpStatus.OK);
        }

        if (item.getCategory() != ItemCategory.DEFAULT.getId()) {
            if (item.getCategory() == ItemCategory.ALPHA.getId() && current.getUsedAlpha() >= current.getMaxAlpha()
                    || item.getCategory() == ItemCategory.BETA.getId() && current.getUsedBeta() >= current.getMaxBeta()
                    || item.getCategory() == ItemCategory.GAMMA.getId() && current.getUsedGamma() >= current.getMaxGamma()
                    || item.getCategory() == ItemCategory.DELTA.getId() && current.getUsedDelta() >= current.getMaxDelta()
                    || item.getCategory() == ItemCategory.LAMBDA.getId() && current.getUsedLambda() >= current.getMaxLambda()) {
                return new ResponseEntity<>("CATEGORY_FULL", HttpStatus.OK);
            } else {
                switch (ItemCategory.of(item.getCategory())) {
                    case ALPHA: current.setUsedAlpha(current.getUsedAlpha() + count); break;
                    case BETA: current.setUsedBeta(current.getUsedBeta() + count); break;
                    case GAMMA: current.setUsedGamma(current.getUsedGamma() + count); break;
                    case DELTA: current.setUsedDelta(current.getUsedDelta() + count); break;
                    case LAMBDA: current.setUsedLambda(current.getUsedLambda() + count); break;
                }
            }
        }

        timewindow.setNormalItemCount(timewindow.getNormalItemCount() - count);
        if (order.isExtraTag())
            timewindow.setExtraItemCount(timewindow.getExtraItemCount() - count);
        
        current.setOrderCount(current.getOrderCount() + count);
        
        order.setDate(timewindow.getDate());
        order.setPrice(order.getPrice() * count);
        order.setIntervalMessage(timewindow.getName());
        order.setCancelUntil(current.getOrderEnd());
        order.setCategory(item.getCategory());
        order.setPriority(user.getOrderingPriority());
        order.setCompactName((item.getAlias().isEmpty() ? item.getName() : item.getAlias()) + (count == 1 ? "" : (" x " + count)));
        order.setCount(count);

        timewindowRepo.save(timewindow);
        openings.save(current);
        save(order);
        
        return new ResponseEntity<>("ACK", HttpStatus.OK);
    }

    @Deprecated
    public ResponseEntity<String> cancelOrder(HttpServletRequest request, long id) {
        try {
            OrderEntity order = getOne(id);
            if (!order.getUserId().equals(util.getUser(request).getUid()))
                return new ResponseEntity<>("BAD_REQUEST", HttpStatus.BAD_REQUEST);

            if (order.getStatus() != OrderStatus.ACCEPTED) 
                return new ResponseEntity<>("INVALID_STATUS", HttpStatus.OK);
            
            OpeningEntity opening = openings.getOne(order.getOpeningId());
            if (opening.getOrderEnd() <= System.currentTimeMillis()) 
                return new ResponseEntity<>("ORDER_PERIOD_ENDED", HttpStatus.OK);

            order.setStatus(OrderStatus.CANCELLED);
            int count = order.getCount();

            TimeWindowEntity timeWindow = timewindowRepo.getOne(order.getIntervalId());
            timeWindow.setNormalItemCount(timeWindow.getNormalItemCount() + count);
            if (order.isExtraTag())
                timeWindow.setExtraItemCount(timeWindow.getExtraItemCount() + count);
            
            opening.setOrderCount(opening.getOrderCount() - count);

            if (order.getCategory() != ItemCategory.DEFAULT.getId()) {
                switch (ItemCategory.of(order.getCategory())) {
                    case ALPHA: opening.setUsedAlpha(opening.getUsedAlpha() - count); break;
                    case BETA: opening.setUsedBeta(opening.getUsedBeta() - count); break;
                    case GAMMA: opening.setUsedGamma(opening.getUsedGamma() - count); break;
                    case DELTA: opening.setUsedDelta(opening.getUsedDelta() - count); break;
                    case LAMBDA: opening.setUsedLambda(opening.getUsedLambda() - count); break;
                }
            }

            timewindowRepo.save(timeWindow);
            openings.save(opening);
            save(order);
            
            return new ResponseEntity<>("ACK", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("BAD_REQUEST", HttpStatus.BAD_REQUEST);
        }
    }

    public List<OrderEntity> findToExport(Long openingId, String orderBy) {
        switch (orderBy) {
            case ORDER_ABSOLUTE:
                return appendArtificialId(repo.findAllByOpeningIdAndStatusNotOrderByPriorityDescDateAsc(
                        openingId, OrderStatus.CANCELLED));

            case ORDER_GROUPED:
                return appendArtificialId(repo.findAllByOpeningIdAndStatusNotOrderByIntervalIdAscPriorityDescDateAsc(
                        openingId, OrderStatus.CANCELLED));

            case ORDER_ROOMS:
                return appendArtificialId(repo.findAllByOpeningIdAndStatusNot(openingId, OrderStatus.CANCELLED)
                        .stream().collect(
                                groupingBy(OrderEntity::getIntervalId, groupingBy(OrderEntity::getRoom)))
                        .entrySet()
                        .stream()
                        .sorted(Comparator.comparing(Map.Entry::getKey))
                        .flatMap(interval -> interval.getValue().values().stream()
                                .sorted((a, b) -> Long.compare(
                                        b.stream().max(Comparator.comparing(OrderEntity::getPriority))
                                                .map(OrderEntity::getPriority).orElse(0),
                                        a.stream().max(Comparator.comparing(OrderEntity::getPriority))
                                                .map(OrderEntity::getPriority).orElse(0)))
                                .flatMap(Collection::stream))
                        .collect(Collectors.toList()));

                default: return appendArtificialId(repo.findAllByOpeningIdAndStatusNotOrderByPriorityDescDateAsc(
                        openingId, OrderStatus.CANCELLED));
        }
    }

    private List<OrderEntity> appendArtificialId(List<OrderEntity> source) {
        int id = 1;
        for (OrderEntity order : source)
            order.setArtificialTransientId(id++);
        return source;
    }

}
