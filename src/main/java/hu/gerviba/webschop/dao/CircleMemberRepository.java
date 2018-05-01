package hu.gerviba.webschop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.gerviba.webschop.model.CircleMemberEntity;

@Repository
public interface CircleMemberRepository extends JpaRepository<CircleMemberEntity, Long> {

}
