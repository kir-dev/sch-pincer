package hu.gerviba.webschop.dao;

import java.net.ContentHandler;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.ItemEntity;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    List<ItemEntity> findAllByCircle_Id(Long circleId);
    
    List<ItemEntity> findAllByVisibleTrue();
    
    List<ItemEntity> findAllByVisibleTrueAndVisibleInAllTrue();
    
    Page<ItemEntity> findAllByVisibleTrueAndVisibleInAllTrue(Pageable page);

    void deleteByCircle_Id(Long circleId);

    List<ItemEntity> findAllByCircle_IdIn(List<Long> circles);

    List<ItemEntity> findAllByVisibleTrueOrderByPrecedenceDesc();

    Page<ItemEntity> findAllByVisibleTrueAndVisibleInAllTrueOrderByPrecedenceDesc(Pageable page);

    List<ItemEntity> findAllByCircle_IdOrderByPrecedenceDesc(Long circleId);

    List<ItemEntity> findAllByCircle_IdInOrderByPrecedenceDesc(List<Long> circles);
}
