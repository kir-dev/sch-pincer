package hu.gerviba.webschop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

}
