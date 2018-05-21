package hu.gerviba.webschop.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.CircleMemberRepository;
import hu.gerviba.webschop.model.CircleMemberEntity;

@Service
@Transactional
public class CircleMemberService {

    @Autowired
    CircleMemberRepository repo;
    
    public CircleMemberEntity getById(Long id) {
        return repo.getOne(id);
    }
    
    public void save(CircleMemberEntity member) {
        repo.save(member);
    }

    public CircleMemberEntity getOne(Long memberId) {
        return repo.getOne(memberId);
    }

    public void delete(CircleMemberEntity cme) {
        repo.delete(cme);
    }
    
}
