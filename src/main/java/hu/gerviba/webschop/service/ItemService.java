package hu.gerviba.webschop.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
	
	public List<ItemEntity> findAll(int page) {
	    return repo.findAll(PageRequest.of(page, 6)).getContent();
	}
	
	public ItemEntity getOne(Long itemId) {
	    return repo.getOne(itemId);
	}
	
    public void save(ItemEntity itemEntity) {
        repo.save(itemEntity);
    }

    public List<ItemEntity> findAllByCircle(Long circleId) {
        return repo.findAllByCircle_Id(circleId);
    }
	
}
