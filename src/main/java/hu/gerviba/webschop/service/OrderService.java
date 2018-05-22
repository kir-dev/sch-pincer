package hu.gerviba.webschop.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.OrderRepository;
import hu.gerviba.webschop.model.OrderEntity;

@Service
@Transactional
public class OrderService {

    @Autowired
    OrderRepository repo;
    
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
    
}
