package hu.gerviba.webschop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.CircleEntity;

@Repository
public interface CircleRepositoriy extends JpaRepository<CircleEntity, Long> {

}
