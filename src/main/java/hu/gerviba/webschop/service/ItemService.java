package hu.gerviba.webschop.service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.ItemRepository;
import hu.gerviba.webschop.dao.OpeningRepository;
import hu.gerviba.webschop.model.ItemEntity;

@Service
@Transactional
public class ItemService {

	@Autowired
	ItemRepository repo;

    @Autowired
    OpeningRepository openingRepo;
	
    private static final int DAY_IN_MILLIS  = 24 * 60 * 60 * 1000;
    private static final int HOUR6_IN_MILLIS  = 6 * 60 * 60 * 1000;

    public List<ItemEntity> findAll() {
		return repo.findAllByVisibleTrueOrderByPrecedenceDesc();
	}
	
	public List<ItemEntity> findAll(int page) {
	    return repo.findAllByVisibleTrueAndVisibleInAllTrueOrderByPrecedenceDesc(PageRequest.of(page, 6)).getContent();
	}
	
	public ItemEntity getOne(Long itemId) {
	    return repo.getOne(itemId);
	}
	
    public void save(ItemEntity itemEntity) {
        repo.save(itemEntity);
    }

    public List<ItemEntity> findAllByCircle(Long circleId) {
        return repo.findAllByCircle_IdOrderByManualPrecedenceDesc(circleId);
    }

    public void delete(ItemEntity ie) {
        repo.delete(ie);
    }
	
    public void deleteByCircle(Long circleId) {
        repo.deleteByCircle_Id(circleId);
    }

    public List<ItemEntity> findAllByOrderableNow() {
        long time = System.currentTimeMillis();
        List<Long> circles = openingRepo.findAllByOrderStartLessThanAndOrderEndGreaterThan(
                    time,
                    time)
                .stream()
                .map(opening -> opening.getCircle().getId())
                .collect(Collectors.toList());
        return repo.findAllByCircle_IdInOrderByPrecedenceDesc(circles);
    }
    
    public List<ItemEntity> findAllByOrerableTomorrow() {
        long time1 = getJustDateFrom(System.currentTimeMillis()) + DAY_IN_MILLIS;
        long time2 = time1 + DAY_IN_MILLIS + HOUR6_IN_MILLIS;
        List<Long> circles = openingRepo.findAllByOrderStartGreaterThanAndOrderStartLessThan(time1, time2)
                .stream()
                .map(opening -> opening.getCircle().getId())
                .collect(Collectors.toList());
        return repo.findAllByCircle_IdInOrderByPrecedenceDesc(circles);
    }
    
    public static long getJustDateFrom(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
    
}
