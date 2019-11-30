package hu.gerviba.webschop.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.OpeningEntity;

@Repository
public interface OpeningRepository extends JpaRepository<OpeningEntity, Long> {

    List<OpeningEntity> findAllByOrderByDateStart();
    
    List<OpeningEntity> findAllByDateEndGreaterThanAndDateEndLessThanOrderByDateStart(long now, long weekFromNow);
    
    List<OpeningEntity> findAllByOrderStartGreaterThanAndOrderStartLessThan(long time1, long time2);
    
    Optional<OpeningEntity> findFirstByCircle_IdOrderByDateStart(Long circle);
    
    Optional<OpeningEntity> findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(Long id, long time);

    List<OpeningEntity> findAllByOrderStartLessThanAndOrderEndGreaterThan(long currentTime1, long currentTime2);

    List<OpeningEntity> findAllByOrderEndGreaterThanOrderByDateStart(long currentTimeMillis);

}
