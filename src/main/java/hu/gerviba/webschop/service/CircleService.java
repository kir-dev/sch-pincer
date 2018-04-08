package hu.gerviba.webschop.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.CircleRepository;
import hu.gerviba.webschop.model.CircleEntity;

@Service
@Transactional
public class CircleService {

    @Autowired
    CircleRepository repo;
    
    public Page<CircleEntity> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    public List<CircleEntity> findAll() {
        return repo.findAll();
    }

    public void add(CircleEntity circleEntity) {
        repo.save(circleEntity);
    }
    
    public CircleEntity getOne(Long id) {
        return repo.getOne(id);
    }
    
}
