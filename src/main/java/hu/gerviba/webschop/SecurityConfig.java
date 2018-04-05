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
                .antMatchers("/static/**", "/", "/index", "/search/**", "/item/**", "/login").permitAll()
                .antMatchers("/profile/**").hasRole("USER")
                .antMatchers("/circle/admin/**").hasRole("LEADER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/loggedin").permitAll()
            .and()
                .formLogin()
                .loginPage("/formlogin");
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
