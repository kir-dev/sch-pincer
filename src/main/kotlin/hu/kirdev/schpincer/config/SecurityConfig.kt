package hu.kirdev.schpincer.config

import hu.kirdev.schpincer.service.CircleService
import hu.kirdev.schpincer.service.SchPincerOidcUserService
import hu.kirdev.schpincer.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain

enum class Role {
    ADMIN, LEADER, USER
}

@Configuration
@EnableWebSecurity
open class SecurityConfig {

    @Bean
    open fun securityFilterChain(
        http: HttpSecurity,
        userService: UserService,
        circleService: CircleService,
        @Value("\${schpincer.sysadmins:}") admins: String
    ): SecurityFilterChain {
        http.authorizeHttpRequests { auth ->
            auth.requestMatchers(
                "/",
                "/index",
                "/circle",
                "/items",
                "/search/**",
                "/cdn/**",
                "/item/**",
                "/items/**",
                "/provider/**",
                "/p/**",
                "/americano",
                "/pizzasch",
                "/reggelisch",
                "/kakas",
                "/langosch",
                "/vodor",
                "/dzsajrosz"
            ).permitAll()
            auth.requestMatchers("/loggedin", "/login").permitAll()
            auth.requestMatchers("/api/**").permitAll()
            auth.requestMatchers("/order/**").hasRole(Role.USER.name)
            auth.requestMatchers("/profile", "/profile/**", "/stats").hasRole(Role.USER.name)
            auth.requestMatchers("/configure/**").hasAnyRole(Role.LEADER.name, Role.ADMIN.name)
            auth.requestMatchers("/admin", "/admin/**").hasRole(Role.ADMIN.name)
            auth.anyRequest().permitAll()
        }.oauth2Login {
            it.userInfoEndpoint { endpoint ->
                endpoint.oidcUserService(oidcUserService(userService, circleService, admins))
            }
        }.csrf { csrf ->
            csrf.ignoringRequestMatchers(
                "/api/**",
                "/configure/order/update",
                "/configure/order/set-comment",
                "/configure/order/change-price",
            )
        }.cors(Customizer.withDefaults())

        return http.build()
    }

    private fun oidcUserService(
        userService: UserService,
        circleService: CircleService,
        admins: String
    ): OAuth2UserService<OidcUserRequest, OidcUser> = SchPincerOidcUserService(userService, circleService, admins)

}
