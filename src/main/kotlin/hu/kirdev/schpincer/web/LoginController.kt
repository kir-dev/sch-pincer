package hu.kirdev.schpincer.web

import hu.gerviba.authsch.AuthSchAPI
import hu.gerviba.authsch.response.ProfileDataResponse
import hu.gerviba.authsch.struct.Scope
import hu.kirdev.schpincer.config.Role
import hu.kirdev.schpincer.model.CardType
import hu.kirdev.schpincer.model.UserEntity
import hu.kirdev.schpincer.service.CircleService
import hu.kirdev.schpincer.service.UserService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*
import javax.servlet.http.HttpServletRequest

const val USER_SESSION_ATTRIBUTE_NAME = "user_id"
const val USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME = "user"
const val CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME = "circles"

@Controller
open class LoginController {

    @Autowired
    private lateinit var authSch: AuthSchAPI

    @Autowired
    private lateinit var users: UserService

    @Autowired
    private lateinit var circles: CircleService

    @Value("\${schpincer.sysadmins:}")
    private lateinit var systemAdmins: String

    @ApiOperation("Login re-entry point")
    @GetMapping("/loggedin")
    fun loggedIn(@RequestParam code: String, @RequestParam state: String, request: HttpServletRequest): String {
        if (buildUniqueState(request) != state)
            return "index?invalid-state"

        var auth: Authentication? = null
        try {
            val response = authSch.validateAuthentication(code)
            val profile = authSch.getProfile(response.accessToken)
            val user: UserEntity
            val ownedCircles = getOwnedCircleIds(profile)
            if (users.exists(profile.internalId.toString())) {
                user = users.getById(profile.internalId.toString())
                val card = cardTypeLookup(profile)
                if (user.cardType !== card) {
                    user.cardType = card
                    users.save(user)
                }
                val permissionsByVIR = getCirclePermissionList(ownedCircles)
                if (!user.permissions.containsAll(permissionsByVIR)) {
                    permissionsByVIR.addAll(user.permissions)
                    user.permissions = permissionsByVIR
                    users.save(user)
                }
            } else {
                val card = cardTypeLookup(profile)
                user = UserEntity(profile.internalId.toString(),
                        profile.surname + " " + profile.givenName,
                        profile.mail,
                        "",
                        false,
                        card, getCirclePermissionList(ownedCircles),
                        1)
                users.save(user)
            }

            if (systemAdmins.split(",").contains(user.uid))
                user.sysadmin = true

            auth = UsernamePasswordAuthenticationToken(code, state, getAuthorities(user))

            request.session.setAttribute(USER_SESSION_ATTRIBUTE_NAME, user.uid)
            request.session.setAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME, user)
            request.session.setAttribute(CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME, ownedCircles)
            SecurityContextHolder.getContext().authentication = auth
        } catch (e: Exception) {
            auth?.isAuthenticated = false
            e.printStackTrace()
        }
        return if (auth != null && auth.isAuthenticated) "redirect:/" else "redirect:/?error"
    }

    private fun getOwnedCircleIds(profile: ProfileDataResponse): List<Long> {
        return profile.eduPersonEntitlements
                .filter { it.status == "körvezető" }
                .mapNotNull { circles.findByVirGroupId(it.id)?.id }
    }

    private fun getCirclePermissionList(circles: List<Long>): MutableSet<String> {
        val permissions = mutableSetOf<String>()
        if (circles.isNotEmpty()) {
            permissions.add("ROLE_LEADER")
            permissions.addAll(circles.map { "CIRCLE_${it}" })
        }
        return permissions
    }

    private fun cardTypeLookup(profile: ProfileDataResponse): CardType {
        var card = CardType.DO
        for (entrant in profile.entrants) {
            if (entrant.entrantType.equals("KB", ignoreCase = true) && card.ordinal < CardType.KB.ordinal) {
                card = CardType.KB
            } else if (entrant.entrantType.matches("^[ÁáAa][Bb]$".toRegex()) && card.ordinal < CardType.AB.ordinal) {
                card = CardType.AB
            }
        }
        return card
    }

    private fun getAuthorities(user: UserEntity): List<GrantedAuthority> {
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

    @ApiOperation("Redirection to the auth provider")
    @GetMapping("/login")
    fun items(request: HttpServletRequest): String {
        return "redirect:" + authSch.generateLoginUrl(buildUniqueState(request),
                Scope.BASIC, Scope.GIVEN_NAME, Scope.SURNAME, Scope.MAIL, Scope.ENTRANTS, Scope.EDU_PERSON_ENTILEMENT)
    }

    fun buildUniqueState(request: HttpServletRequest): String {
        return (request.session.id
                + request.localAddr
                + request.localPort).sha256()
    }

    @ApiOperation("Logout user")
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        request.removeAttribute(USER_SESSION_ATTRIBUTE_NAME)
        request.removeAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME)
        request.removeAttribute(CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME)
        request.session.removeAttribute(USER_SESSION_ATTRIBUTE_NAME)
        request.session.removeAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME)
        request.session.removeAttribute(CIRCLE_OWNERSHIP_SESSION_ATTRIBUTE_NAME)
        request.changeSessionId()
        return "redirect:/?logged-out"
    }

}
