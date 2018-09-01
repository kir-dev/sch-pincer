package hu.gerviba.webschop.web;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hu.gerviba.authsch.AuthSchAPI;
import hu.gerviba.authsch.response.AuthResponse;
import hu.gerviba.authsch.response.ProfileDataResponse;
import hu.gerviba.authsch.struct.Scope;
import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value="onlinestore", description="Custom OAuth2")
public class LoginController {

    @Autowired
    private AuthSchAPI authSch;

    @Autowired
    private UserService users;
    
    @ApiOperation("Login re-entry point")
    @GetMapping("/loggedin")
    public String loggedIn(@RequestParam String code, @RequestParam String state, HttpServletRequest request) {
        if (!buildUniqueState(request).equals(state))
            return "index?invalid-state";
        
        Authentication auth = null;
        try {
            AuthResponse response = authSch.validateAuthentication(code);
            ProfileDataResponse profile = authSch.getProfile(response.getAccessToken());
            
            if (users.exists(profile.getInternalId().toString())) {
                UserEntity user = users.getById(profile.getInternalId().toString());
            	request.getSession().setAttribute("user", user);
                auth = new UsernamePasswordAuthenticationToken(code, state, getAuthorities(user));
            } else {
            	UserEntity user = new UserEntity(profile.getInternalId().toString(), 
                        profile.getSurname() + " " + profile.getGivenName(), 
                        profile.getMail());
//            	user.setSysadmin(true);
                users.save(user);
                auth = new UsernamePasswordAuthenticationToken(code, state, getAuthorities(user));
                request.getSession().setAttribute("user", user);
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            
        } catch (Exception e) {
            if(auth != null)
                auth.setAuthenticated(false);
            e.printStackTrace();
        }
        
        return (auth != null && auth.isAuthenticated()) ? "redirect:/" : "redirect:/?error";
    }

    private List<GrantedAuthority> getAuthorities(UserEntity user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.isSysadmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_LEADER"));
        }
        if (user.getPermissions().contains("ROLE_LEADER"))
            authorities.add(new SimpleGrantedAuthority("ROLE_LEADER"));
        
        return authorities;
    }

    @ApiOperation("Redirection to the auth provider")
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

    @ApiOperation("Logout user")
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
    	request.removeAttribute("user");
    	request.getSession().removeAttribute("user");
    	request.changeSessionId();
//    	SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
    	return "redirect:/?logged-out";
    }
    
}
