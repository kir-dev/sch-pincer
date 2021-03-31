package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.UserRepository
import hu.kirdev.schpincer.dto.CircleRoleEntryDto
import hu.kirdev.schpincer.model.UserEntity
import hu.kirdev.schpincer.web.sha256
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.NoSuchAlgorithmException
import java.util.*

@Service
open class UserService {

    @Autowired
    private lateinit var repo: UserRepository

    @Transactional(readOnly = true)
    open fun getById(uid: String): UserEntity {
        return repo.getOne(uid)
    }

    @Transactional(readOnly = true)
    open fun exists(uid: String): Boolean {
        return repo.existsById(uid)
    }

    @Transactional(readOnly = false)
    open fun save(user: UserEntity) {
        repo.save(user)
    }

    @Transactional(readOnly = true)
    open fun findAll(): List<UserEntity> {
        return repo.findAll()
    }

    @Transactional(readOnly = true)
    open fun findAllCircleRole(circle_id: Long): List<CircleRoleEntryDto> {
        return repo.findAll()
                .filter { !it.sysadmin }
                .map { CircleRoleEntryDto(it, circle_id) }
                .sortedWith(compareBy<CircleRoleEntryDto> { it.permission }.thenBy { it.name })
    }

    @Transactional(readOnly = true)
    open fun findPermissionByUidHash(uidHash: String, circle_id: Long): CircleRoleEntryDto? {
        val user: UserEntity = getByUidHash(uidHash) ?: return null
        return CircleRoleEntryDto(user, circle_id)
    }

    @Transactional(readOnly = true)
    open fun getByUidHash(uidHash: String): UserEntity? {
        val users: List<UserEntity> = repo.findAll()
        return users.stream().filter {
            try {
                return@filter it.uid.sha256().equals(uidHash, ignoreCase = true)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            false
        }.findAny().orElse(null)
    }

    @Transactional(readOnly = false)
    open fun grantAdmin(uid: String): Boolean {
        val req: Optional<UserEntity> = repo.findById(uid)
        if (!req.isPresent())
            return false
        val user: UserEntity = req.get()
        user.sysadmin = true
        repo.save(user)
        return true
    }

    @Transactional(readOnly = false)
    open fun setRoom(userId: String, room: String): UserEntity {
        val user = getById(userId)
        user.room = room
        repo.save(user)
        return user
    }

}
