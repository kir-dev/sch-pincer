package hu.gerviba.webschop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.UserRepository;
import hu.gerviba.webschop.model.UserEntity;

@Service
public class UserService {

    @Autowired
    UserRepository users;
    
    public UserEntity getById(String uid) {
        return users.getOne(uid);
    }
    
    public boolean exists(String uid) {
        return users.existsById(uid);
    }
    
    public void save(UserEntity user) {
        users.save(user);
    }

}
