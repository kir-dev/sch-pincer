package hu.gerviba.webschop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.CircleMemberRepository;
import hu.gerviba.webschop.model.CircleMemberEntity;

@Service
public class CircleMemberService {

    @Autowired
    CircleMemberRepository members;
    
    public CircleMemberEntity getById(Long id) {
        return members.getOne(id);
    }
    
    public void save(CircleMemberEntity member) {
        members.save(member);
    }
    
}
