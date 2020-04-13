package hu.kirdev.schpincer.config

import hu.gerviba.authsch.AuthSchAPI
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

const val ADMIN_ROLE  = "ADMIN"
const val LEADER_ROLE = "LEADER"
const val USER_ROLE   = "USER"

@EnableWebSecurity
@Configuration
open class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/", "/index", "/circle", "/items", "/search/**",
                        "/item/**", "/items/**", "/provider/**", "/p/**",
                        "/americano", "/pizzasch", "/reggelisch", "/kakas", "/langosch", "/vodor", "/dzsajrosz").permitAll()
                .antMatchers("/loggedin", "/login").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/profile", "/profile/**").hasRole(USER_ROLE)
                .antMatchers("/configure/**").hasAnyRole(LEADER_ROLE, ADMIN_ROLE)
                .antMatchers("/admin", "/admin/**").hasRole(ADMIN_ROLE)
                .antMatchers("/admin", "/admin/**").hasRole(USER_ROLE)
                .and()
                .formLogin()
                .loginPage("/login")
        http.csrf().ignoringAntMatchers("/api/**", "/configure/order/update")
    }

    @Autowired
    @Throws(Exception::class)
    open fun configureGlobal(auth: AuthenticationManagerBuilder?) {
    }

    @Bean
    @ConfigurationProperties(prefix = "authsch")
    open fun authSchApi(): AuthSchAPI {
        return AuthSchAPI()
    }
}
