package hu.gerviba.webschop.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.gerviba.webschop.model.CircleMemberEntity;

public interface CircleMemberRepository extends JpaRepository<CircleMemberEntity, Long> {

}
