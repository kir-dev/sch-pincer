package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.UserRepository
import hu.kirdev.schpincer.model.UserEntity
import hu.kirdev.schpincer.web.ControllerUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.transaction.Transactional


@Service
@Transactional
open class UserService {

    @Autowired
    private lateinit var repo: UserRepository

    // TODO: Refactor: use the extension functions
    @Autowired
    private lateinit var util: ControllerUtil

    open fun getById(uid: String): UserEntity {
        return repo.getOne(uid)
    }

    open fun exists(uid: String): Boolean {
        return repo.existsById(uid)
    }

    open fun save(user: UserEntity) {
        repo.save(user)
    }

    open fun findAll(): List<UserEntity> {
        return repo.findAll()
    }

    // TODO: Refactor: use the extension sha512 function
    open fun getByUidHash(uidHash: String): UserEntity? {
        val users: List<UserEntity> = repo.findAll()
        return users.stream().filter { x: UserEntity ->
            try {
                return@filter util.sha256(x.uid).equals(uidHash, ignoreCase = true)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            false
        }.findAny().orElse(null)
    }

    open fun grantAdmin(uid: String): Boolean {
        val req: Optional<UserEntity> = repo.findById(uid)
        if (!req.isPresent())
            return false
        val user: UserEntity = req.get()
        user.sysadmin = true
        repo.save(user)
        return true
    }

}