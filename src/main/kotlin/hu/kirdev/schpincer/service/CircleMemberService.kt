package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.CircleMemberRepository
import hu.kirdev.schpincer.model.CircleMemberEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class CircleMemberService {

    @Autowired
    private lateinit var repo: CircleMemberRepository

    @Transactional(readOnly = true)
    open fun getById(id: Long): CircleMemberEntity {
        return repo.getOne(id)
    }

    @Transactional(readOnly = false)
    open fun save(member: CircleMemberEntity) {
        repo.save(member)
    }

    @Transactional(readOnly = true)
    open fun getOne(memberId: Long): CircleMemberEntity {
        return repo.getOne(memberId)
    }

    @Transactional(readOnly = false)
    open fun delete(cme: CircleMemberEntity) {
        repo.delete(cme)
    }

}
