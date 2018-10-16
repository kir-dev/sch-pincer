package hu.gerviba.webschop.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.OpeningRepository;
import hu.gerviba.webschop.model.OpeningEntity;

@Service
@Transactional
public class OpeningService {
    
    @Autowired
    OpeningRepository repo;

    private static final long WEEK = 1000 * 60 * 60 * 24 * 7;
    
    public List<OpeningEntity> findAll() {
        return repo.findAllByOrderByDateStart();
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
        return repo.findFirstByCircle_IdOrderByDateStart(id).orElse(null);
    }
    
    public Long findNextStartDateOf(Long id) {
        Optional<OpeningEntity> opening = repo.findFirstByCircle_IdOrderByDateStart(id);
        return opening.isPresent() ? opening.get().getDateStart() : null;
    }

    public OpeningEntity getOne(Long openingId) {
        return repo.getOne(openingId);
    }

    public void delete(OpeningEntity oe) {
        repo.delete(oe);
    }
    
}
