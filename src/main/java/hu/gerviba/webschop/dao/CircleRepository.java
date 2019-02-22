package hu.gerviba.webschop.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.CircleEntity;

@Repository
public interface CircleRepository extends JpaRepository<CircleEntity, Long> {

    List<CircleEntity> findAllByOrderByHomePageOrder();

    CircleEntity findByAlias(String alias);
    
}
