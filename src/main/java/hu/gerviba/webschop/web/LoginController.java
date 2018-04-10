package hu.gerviba.webschop.web;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hu.gerviba.authsch.AuthSchAPI;
import hu.gerviba.authsch.response.AuthResponse;
import hu.gerviba.authsch.response.ProfileDataResponse;
import hu.gerviba.authsch.struct.Scope;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private AuthSchAPI authSch;

    @Autowired
    private UserService users;
    
    @GetMapping("/loggedin")
    public String loggedIn(@RequestParam String code, @RequestParam String state, HttpServletRequest request) {
        if (!buildUniqueState(request).equals(state))
            return "index?invalid-state";

        Authentication auth = new UsernamePasswordAuthenticationToken(code, state, getAuthorities());
        try {
            AuthResponse response = authSch.validateAuthentication(auth.getName());
            ProfileDataResponse profile = authSch.getProfile(response.getAccessToken());
            
            if (users.exists(profile.getInternalId().toString())) {
            	request.getSession().setAttribute("user", users.getById(profile.getInternalId().toString()));
            } else {
            	UserEntity user = new UserEntity(profile.getInternalId().toString(), 
                        profile.getSurname() + " " + profile.getGivenName(), 
                        profile.getMail());
                users.save(user);
                request.getSession().setAttribute("user", user);
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            
        } catch (Exception e) {
            auth.setAuthenticated(false);
            e.printStackTrace();
        }
        
        return auth.isAuthenticated() ? "redirect:/" : "redirect:/?error";
    }

    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            private static final long serialVersionUID = -8093016144065421718L;

            public String getAuthority() {
                return "ROLE_USER";
            }
        };
        grantedAuthorities.add(grantedAuthority);
        return grantedAuthorities;
    }

    @GetMapping("/login")
    public String items(HttpServletRequest request) {
        return "redirect:" + authSch.generateLoginUrl(buildUniqueState(request),
                Scope.BASIC, Scope.GIVEN_NAME, Scope.SURNAME, Scope.MAIL);
    }

    static String buildUniqueState(HttpServletRequest request) {
        return hashString(request.getSession().getId()
                + request.getLocalAddr()
                + request.getLocalPort());
    }

    static final String hashString(String in) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return String.format("%064x", new BigInteger(1, digest.digest(in.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "error";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
    	request.changeSessionId();
    	request.removeAttribute("user");
    	return "redirect:/?logged-out";
    }
    
}
