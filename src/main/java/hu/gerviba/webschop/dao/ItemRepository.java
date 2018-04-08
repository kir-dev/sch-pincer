package hu.gerviba.webschop.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.gerviba.webschop.model.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

//	List<ItemEntity> findAllByLimit(int limit);
	
}
