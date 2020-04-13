package hu.kirdev.schpincer.dto

import hu.kirdev.schpincer.model.UserEntity

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
