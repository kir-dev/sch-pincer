package hu.gerviba.webschop.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.ItemRepository;
import hu.gerviba.webschop.model.ItemEntity;

@Service
@Transactional
public class ItemService {

	@Autowired
	ItemRepository repo;
	
	public List<ItemEntity> findAll() {
		return repo.findAll();
	}
	
    public void save(ItemEntity itemEntity) {
        repo.save(itemEntity);
    }
	
}
