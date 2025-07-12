package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.config.Role
import hu.kirdev.schpincer.model.SchPincerOidcUser
import hu.kirdev.schpincer.model.UserEntity
import hu.kirdev.schpincer.web.getOwnedCircleIds
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.transaction.annotation.Transactional

open class SchPincerOidcUserService(
    private val userService: UserService,
    private val circleService: CircleService,
    admins: String
) : OidcUserService() {
    private val admins: List<String> = admins.split(Regex(", *"))

    @Transactional(readOnly = false)
    override fun loadUser(userRequest: OidcUserRequest?): OidcUser? {
        val authschUser = super.loadUser(userRequest) ?: return null

        val schPincerUser = SchPincerOidcUser(authschUser)
        val ownedCircles = getOwnedCircleIds(schPincerUser.memberships, circleService)
        if (userService.exists(schPincerUser.internalId)) {
            val user = userService.getById(schPincerUser.internalId)
            user.email = schPincerUser.email
            if (admins.contains(user.uid))
                user.sysadmin = true
            val card = schPincerUser.cardType
            if (user.pekCardType !== card) {
                user.pekCardType = card
            }
            if (user.orderingPriority == 0)
                user.orderingPriority = 1
            val permissionsByVIR = getCirclePermissionList(ownedCircles)
            if (!user.permissions.containsAll(permissionsByVIR)) {
                permissionsByVIR.addAll(user.permissions)
                user.permissions = permissionsByVIR
            }
            schPincerUser.extraAuthorities = getAuthoritiesFromEntity(user)
            userService.save(user)
        } else {
            val card = schPincerUser.cardType
            val user = UserEntity(
                schPincerUser.internalId,
                schPincerUser.name,
                schPincerUser.email,
                "",
                admins.contains(schPincerUser.internalId),
                card,
                false,
                getCirclePermissionList(ownedCircles),
                1
            )
            schPincerUser.extraAuthorities = getAuthoritiesFromEntity(user)
            userService.save(user)
        }
        return schPincerUser
    }

    private fun getCirclePermissionList(ownedCircles: List<Long>): MutableSet<String> {
        val permissions = mutableSetOf<String>()
        if (ownedCircles.isNotEmpty()) {
            permissions.add("ROLE_LEADER")
            permissions.addAll(ownedCircles.map { "CIRCLE_${it}" })
        }
        return permissions
    }

    private fun getAuthoritiesFromEntity(user: UserEntity): List<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority("ROLE_${Role.USER.name}"))
        if (user.sysadmin) {
            authorities.add(SimpleGrantedAuthority("ROLE_${Role.ADMIN.name}"))
            authorities.add(SimpleGrantedAuthority("ROLE_${Role.LEADER.name}"))
        }
        if (user.permissions.contains("ROLE_${Role.LEADER.name}"))
            authorities.add(SimpleGrantedAuthority("ROLE_${Role.LEADER.name}"))
        return authorities
    }

}
