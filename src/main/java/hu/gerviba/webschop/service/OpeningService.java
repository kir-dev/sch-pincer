package hu.gerviba.webschop.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.OpeningRepository;
import hu.gerviba.webschop.dao.TimeWindowRepository;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.model.TimeWindowEntity;

import javax.transaction.Transactional;

@Service
@Transactional
public class OpeningService {

    public static final SimpleDateFormat DATE_FORMATTER_HH_MM = new SimpleDateFormat("HH:mm"); 
    
    @Autowired
    OpeningRepository repo;

    @Autowired
    TimeWindowRepository twRepo;

    private static final long WEEK = 1000 * 60 * 60 * 24 * 7;
    
    public List<OpeningEntity> findAll() {
        return repo.findAllByOrderByDateStart();
    }

    public List<OpeningEntity> findUpcomingOpenings() {
        return repo.findAllByOrderEndGreaterThanOrderByDateStart(System.currentTimeMillis());
    }

    public List<OpeningEntity> findNextWeek() {
        return repo.findAllByDateEndGreaterThanAndDateEndLessThanOrderByDateStart(
                System.currentTimeMillis(), 
                System.currentTimeMillis() + WEEK);
    }
    
    public void save(OpeningEntity opening) {
        repo.save(opening);
    }
    
    public OpeningEntity findNextOf(Long id) {
        return repo.findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(id, System.currentTimeMillis()).orElse(null);
    }
    
    public Long findNextStartDateOf(Long id) {
        Optional<OpeningEntity> opening = repo.findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(id, System.currentTimeMillis());
        return opening.map(OpeningEntity::getDateStart).orElse(null);
    }

    public OpeningEntity getOne(Long openingId) {
        return repo.getOne(openingId);
    }

    public void delete(OpeningEntity oe) {
        repo.delete(oe);
    }

    public void saveTimeWindow(TimeWindowEntity tw) {
        twRepo.save(tw);
    }
    
}
