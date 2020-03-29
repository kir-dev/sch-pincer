package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.CircleMemberRepository
import hu.kirdev.schpincer.model.CircleMemberEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
open class CircleMemberService {

    @Autowired
    private lateinit var repo: CircleMemberRepository

    open fun getById(id: Long): CircleMemberEntity {
        return repo.getOne(id)
    }

    open fun save(member: CircleMemberEntity) {
        repo.save(member)
    }

    open fun getOne(memberId: Long): CircleMemberEntity {
        return repo.getOne(memberId)
    }

    open fun delete(cme: CircleMemberEntity) {
        repo.delete(cme)
    }

}
