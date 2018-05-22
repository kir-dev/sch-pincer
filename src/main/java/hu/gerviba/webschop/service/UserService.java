package hu.gerviba.webschop.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.gerviba.webschop.dao.UserRepository;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.web.ControllerUtil;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository repo;
    
    @Autowired
    ControllerUtil util;
    
    public UserEntity getById(String uid) {
        return repo.getOne(uid);
    }
    
    public boolean exists(String uid) {
        return repo.existsById(uid);
    }
    
    public void save(UserEntity user) {
        repo.save(user);
    }

    public List<UserEntity> findAll() {
        return repo.findAll();
    }

    public UserEntity getByUidHash(String uidHash) {
        List<UserEntity> users = repo.findAll();
        return users.stream().filter(x -> {
            try {
                return util.sha256(x.getUid()).equalsIgnoreCase(uidHash);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return false;
        }).findAny().orElse(null);
    }

}
