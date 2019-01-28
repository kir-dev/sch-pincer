package hu.gerviba.webschop.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.OpeningRepository;
import hu.gerviba.webschop.dao.OrderRepository;
import hu.gerviba.webschop.model.OrderEntity;
import hu.gerviba.webschop.model.OrderStatus;

@Service
@Transactional
public class OrderService {

    @Autowired
    OrderRepository repo;
    
    @Autowired
    OpeningRepository openingRepo;
    
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
    
}
