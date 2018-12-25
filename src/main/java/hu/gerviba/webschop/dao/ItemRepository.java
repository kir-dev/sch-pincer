package hu.gerviba.webschop.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.ItemEntity;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    List<ItemEntity> findAllByCircle_Id(Long circleId);
    
    List<ItemEntity> findAllByVisibleTrue();
    
    Page<ItemEntity> findAllByVisibleTrue(Pageable page);

    void deleteByCircle_Id(Long circleId);

}
