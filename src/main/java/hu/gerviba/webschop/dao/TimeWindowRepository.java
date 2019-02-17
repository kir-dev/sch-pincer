package hu.gerviba.webschop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.TimeWindowEntity;

@Repository
public interface TimeWindowRepository extends JpaRepository<TimeWindowEntity, Long> {

}
