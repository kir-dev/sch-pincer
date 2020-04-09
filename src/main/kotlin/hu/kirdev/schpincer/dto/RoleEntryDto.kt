package hu.gerviba.webschop.dto

import hu.kirdev.schpincer.model.UserEntity

class RoleEntryDto(val uidHash: String, ue: UserEntity) {
    val name: String
    val sysadmin: Boolean
    val permissions: String

    init {
        name = ue.name
        sysadmin = ue.sysadmin
        permissions = if (ue.permissions.size == 0) "-" else ue.permissions.joinToString(", ")
    }

}
