package hu.gerviba.webschop.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.ReviewRepository;
import hu.gerviba.webschop.model.ReviewEntity;

@Service
@Transactional
public class ReviewService {

	@Autowired
	ReviewRepository repo;
	
	public List<ReviewEntity> findAll() {
		return repo.findAll();
	}
	
	public void save(ReviewEntity review) {
	    repo.save(review);
	}

    public List<ReviewEntity> findAll(Long circleId) {
        return repo.findAllByCircle_Id(circleId);
    }
	
}
