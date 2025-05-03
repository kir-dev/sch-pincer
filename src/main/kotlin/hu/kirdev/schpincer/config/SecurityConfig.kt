package hu.kirdev.schpincer.config

import hu.gerviba.authsch.AuthSchAPI
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

enum class Role {
    ADMIN, LEADER, USER
}

@Configuration
@EnableWebSecurity
open class SecurityConfig {

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
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
            auth.requestMatchers("/profile", "/profile/**", "/stats").hasRole(Role.USER.name)
            auth.requestMatchers("/configure/**").hasAnyRole(Role.LEADER.name, Role.ADMIN.name)
            auth.requestMatchers("/admin", "/admin/**").hasRole(Role.ADMIN.name)
            auth.anyRequest().permitAll()
        }.formLogin { form ->
            form.loginPage("/login")
        }.csrf { csrf ->
            csrf.ignoringRequestMatchers(
                "/api/**",
                "/configure/order/update",
                "/configure/order/set-comment",
                "/configure/order/change-price",
            )
        }

        return http.build()
    }

    @Bean
    @ConfigurationProperties(prefix = "authsch")
    open fun authSchApi(): AuthSchAPI {
        return AuthSchAPI()
    }
}
