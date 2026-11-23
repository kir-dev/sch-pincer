package hu.kirdev.schpincer.dto

import hu.kirdev.schpincer.model.UserEntity
import hu.kirdev.schpincer.web.toReadableRole

class RoleEntryDto(val uid: String, ue: UserEntity) {
    val name: String = ue.name
    val sysadmin: Boolean = ue.sysadmin
    val permissions: String = if (ue.permissions.isEmpty()) "-" else ue.permissions.joinToString(", ")
}

enum class CircleMemberRole {
    LEADER,
    PR,
    NONE;
}

class CircleRoleEntryDto(ue: UserEntity, circleId: Long) {
    val uid: String = ue.uid
    val name: String = ue.name
    val permission: CircleMemberRole = toReadableRole(ue.permissions, circleId)
}
