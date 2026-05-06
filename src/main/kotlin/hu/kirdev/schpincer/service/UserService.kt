package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.UserRepository
import hu.kirdev.schpincer.dto.CircleRoleEntryDto
import hu.kirdev.schpincer.model.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
open class UserService(private val repo: UserRepository) {

    @Transactional(readOnly = true)
    open fun getById(uid: String): UserEntity {
        return repo.getReferenceById(uid)
    }

    @Transactional(readOnly = true)
    open fun getByIdOrNull(uid: String): UserEntity? {
        return repo.findById(uid).orElse(null)
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
    open fun findAllCircleRole(circleId: Long): List<CircleRoleEntryDto> {
        return repo.findAll()
            .filter { !it.sysadmin }
            .map { CircleRoleEntryDto(it, circleId) }
            .sortedWith(compareBy<CircleRoleEntryDto> { it.permission }.thenBy { it.name })
    }

    @Transactional(readOnly = true)
    open fun findPermissionByUid(uid: String, circleId: Long): CircleRoleEntryDto? {
        val user: UserEntity = getByIdOrNull(uid) ?: return null
        return CircleRoleEntryDto(user, circleId)
    }

    @Transactional(readOnly = false)
    open fun grantAdmin(uid: String): Boolean {
        val req: Optional<UserEntity> = repo.findById(uid)
        if (!req.isPresent)
            return false
        val user: UserEntity = req.get()
        user.sysadmin = true
        repo.save(user)
        return true
    }

    @Transactional(readOnly = false)
    open fun setRoom(userId: String, room: String): UserEntity {
        val user = getById(userId)
        user.room = room.replace(Regex("[^A-Za-z0-9 -_()]"), "")
        repo.save(user)
        return user
    }

    @Transactional(readOnly = true)
    open fun findByUsernameContains(name: String): List<UserEntity> {
        return repo.findTop10AllByNameContainsIgnoreCase(name)
    }

    @Transactional(readOnly = true)
    open fun findByRoomContains(room: String): List<UserEntity> {
        return repo.findTop10AllByRoomContainsIgnoreCase(room)
    }

}
