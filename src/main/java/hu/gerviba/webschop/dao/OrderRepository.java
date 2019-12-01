package hu.gerviba.webschop.dao;

import hu.gerviba.webschop.model.OrderEntity;
import hu.gerviba.webschop.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findAllByUserIdOrderByDateDesc(String userId);

    List<OrderEntity> findAllByOpeningId(Long openingId);

    List<OrderEntity> findAllByOpeningIdOrderByIntervalIdAscPriorityDescDateAsc(Long openingId);

    List<OrderEntity> findAllByOpeningIdOrderByPriorityDescDateAsc(Long openingId);

    List<OrderEntity> findAllByOpeningIdAndStatusNotOrderByPriorityDescDateAsc(Long openingId, OrderStatus status);

    List<OrderEntity> findAllByOpeningIdAndStatusNotOrderByIntervalIdAscPriorityDescDateAsc(Long openingId, OrderStatus status);

    List<OrderEntity> findAllByOpeningIdAndStatusNot(Long openingId, OrderStatus status);


}
