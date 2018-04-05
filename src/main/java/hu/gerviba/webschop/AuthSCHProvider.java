package hu.gerviba.webschop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import hu.gerviba.authsch.AuthSchAPI;
import hu.gerviba.authsch.AuthSchResponseException;
import hu.gerviba.authsch.response.ProfileDataResponse;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.UserService;

public class AuthSCHProvider implements AuthenticationProvider {

    @Autowired
    AuthSchAPI authSchApi;
    
    @Autowired
    UserService users;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("Login geco");
        try {
            ProfileDataResponse profile = authSchApi.getProfile(authentication.getName());
            if (users.exists(profile.getInternalId().toString())) {
                authentication.setAuthenticated(true);
            } else {
                users.save(new UserEntity(profile.getInternalId().toString(), 
                        profile.getSurname() + " " + profile.getGivenName(), 
                        profile.getMail()));
                authentication.setAuthenticated(true);
            }
            
        } catch (AuthSchResponseException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
//        authentication.setAuthenticated(true);
        
//        authentication.getAuthorities().add(new SimpleGrantedAuthority("USER"));
        
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
