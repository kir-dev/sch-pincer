package hu.gerviba.webschop.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.gerviba.webschop.model.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

}
