package hu.gerviba.webschop.service;

import java.util.List;

import javax.transaction.Transactional;

import hu.gerviba.webschop.dao.CircleEntityInfo;
import hu.gerviba.webschop.dao.OpeningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.CircleRepository;
import hu.gerviba.webschop.model.CircleEntity;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class CircleService {

    @Autowired
    CircleRepository repo;

    @Autowired
    OpeningRepository openings;

    public Page<CircleEntity> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    public List<CircleEntity> findAll() {
        return repo.findAll();
    }

    public List<CircleEntity> findAllForMenu() {
        return repo.findAllByVisibleTrueOrderByHomePageOrder();
    }

    public List<CircleEntityInfo> findAllForInfo() {
        return repo.findAllByVisibleTrueOrderByHomePageOrder().stream()
                .map(CircleEntityInfo::new)
                .peek(info -> info.setNextOpening(openings
                        .findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(
                                info.getCircleEntity().getId(), System.currentTimeMillis())
                        .orElse(null)))
                .collect(toList());
    }

    public void save(CircleEntity circleEntity) {
        repo.save(circleEntity);
    }
    
    public CircleEntity getOne(Long id) {
        return repo.getOne(id);
    }

    public void delete(CircleEntity ce) {
        repo.delete(ce);
    }

    public CircleEntity findByAlias(String alias) {
        return repo.findAllByAlias(alias).get(0);
    }
    
}
