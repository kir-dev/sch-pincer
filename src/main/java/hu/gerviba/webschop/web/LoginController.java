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
    UserService users;
    
    @GetMapping("/loggedin")
    @ResponseBody
    public String loggedIn(@RequestParam String code, @RequestParam String state, HttpServletRequest request) {
        if (!buildUnique(request).equals(state))
            return "invalid-state";

        Authentication auth = new UsernamePasswordAuthenticationToken(code, state, getAuthorities());
        try {
            AuthResponse response = authSch.validateAuthentication(auth.getName());
            System.out.println(response);
            ProfileDataResponse profile = authSch.getProfile(response.getAccessToken());
            System.out.println(profile);
            
            if (users.exists(profile.getInternalId().toString())) {
            } else {
                users.save(new UserEntity(profile.getInternalId().toString(), 
                        profile.getSurname() + " " + profile.getGivenName(), 
                        profile.getMail()));
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            
        } catch (Exception e) {
            auth.setAuthenticated(false);
            e.printStackTrace();
        }
        
        return auth.isAuthenticated() ? "t" : "f";
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
        return "redirect:" + authSch.generateLoginUrl(buildUnique(request),
                Scope.BASIC, Scope.GIVEN_NAME, Scope.SURNAME, Scope.MAIL);
    }

    static String buildUnique(HttpServletRequest request) {
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

}
