package hu.gerviba.webschop.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.OpeningEntity;

@Repository
public interface OpeningRepositoriy extends JpaRepository<OpeningEntity, Long> {

    List<OpeningEntity> findAllByOrderByDateStart();
    
    List<OpeningEntity> findAllByDateEndGreaterThanAndDateEndLessThanOrderByDateStart(long now, long weekFromNow);
    
}
