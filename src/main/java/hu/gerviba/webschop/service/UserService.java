package hu.gerviba.webschop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.UserRepositoriy;
import hu.gerviba.webschop.model.UserEntity;

@Service
public class UserService {

    @Autowired
    UserRepositoriy users;
    
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
