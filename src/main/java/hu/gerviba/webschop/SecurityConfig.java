package hu.gerviba.webschop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import hu.gerviba.authsch.AuthSchAPI;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/index", "/circle", "/items", "/search/**", 
                		"/item/**", "/items/**").permitAll()
                .antMatchers("/loggedin", "/login").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/profile", "/profile/**").hasRole("USER")
                .antMatchers("/configure/**").hasAnyRole("LEADER", "ADMIN")
                .antMatchers("/admin", "/admin/**").hasRole("ADMIN")
                .antMatchers("/admin", "/admin/**").hasRole("USER")
            .and()
                .formLogin()
                .loginPage("/login");
        http.csrf().ignoringAntMatchers("/api/**", "/configure/order/update");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth;
    }
    
    @Bean
    @ConfigurationProperties(prefix = "authsch")
    public AuthSchAPI authSchApi() {
        AuthSchAPI api = new AuthSchAPI();
        return api;
    }
    
}
