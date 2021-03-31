package hu.kirdev.schpincer.dto

import hu.kirdev.schpincer.model.UserEntity
import hu.kirdev.schpincer.web.sha256
import hu.kirdev.schpincer.web.toReadableRole

class RoleEntryDto(val uidHash: String, ue: UserEntity) {
    val name: String
    val sysadmin: Boolean
    val permissions: String

    init {
        name = ue.name
        sysadmin = ue.sysadmin
        permissions = if (ue.permissions.isEmpty()) "-" else ue.permissions.joinToString(", ")
    }

}

enum class CircleMemberRole {
    LEADER,
    PR,
    NONE;
}

class CircleRoleEntryDto(ue: UserEntity, circleId: Long) {
    val uidHash: String
    val name: String
    val permission: CircleMemberRole

    init {
        uidHash = ue.uid.sha256()
        name = ue.name
        permission = toReadableRole(ue.permissions, circleId)
    }
}
