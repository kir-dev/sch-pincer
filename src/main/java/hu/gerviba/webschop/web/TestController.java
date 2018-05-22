package hu.gerviba.webschop.web;

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
import org.springframework.web.bind.annotation.RequestMapping;

import hu.gerviba.webschop.model.UserEntity;
import hu.gerviba.webschop.service.UserService;

//@Profile("test")
@RequestMapping("/test/")
@Controller
public class TestController {

    @Autowired
    private UserService users;
    
    @GetMapping("/login/simple")
    public String loginSimpleUser(HttpServletRequest request) {
        Authentication auth = null;
        try {
            
            if (users.exists("66a7b238-5f0d-482d-a357-bb2407906883")) {
                UserEntity user = users.getById("66a7b238-5f0d-482d-a357-bb2407906883");
                request.getSession().setAttribute("user", user);
                auth = new UsernamePasswordAuthenticationToken("test1", "test1", getAuthorities(user));
            } else {
                UserEntity user = new UserEntity("66a7b238-5f0d-482d-a357-bb2407906883", "Simple User", "simpleuser@email.com");
                user.setRoom(906);
                users.save(user);
                auth = new UsernamePasswordAuthenticationToken("test1", "test1", getAuthorities(user));
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
    
    @GetMapping("/login/leader")
    public String loginLeader(HttpServletRequest request) {
        Authentication auth = null;
        try {
            
            if (users.exists("4f2d8797-0a22-404a-9ce4-7420a8cb6c1f")) {
                UserEntity user = users.getById("4f2d8797-0a22-404a-9ce4-7420a8cb6c1f");
                request.getSession().setAttribute("user", user);
                auth = new UsernamePasswordAuthenticationToken("test2", "test2", getAuthorities(user));
            } else {
                UserEntity user = new UserEntity("4f2d8797-0a22-404a-9ce4-7420a8cb6c1f", "Leader User", "leaderuser@email.com");
                user.getPermissions().add("ROLE_LEADER");
                user.getPermissions().add("CIRCLE_1");
                user.setRoom(1020);
                users.save(user);
                auth = new UsernamePasswordAuthenticationToken("test2", "test2", getAuthorities(user));
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

    @GetMapping("/login/admin")
    public String loginAdmin(HttpServletRequest request) {
        Authentication auth = null;
        try {
            
            if (users.exists("b6fa75f0-8399-4869-9438-42da2baca30d")) {
                UserEntity user = users.getById("b6fa75f0-8399-4869-9438-42da2baca30d");
                request.getSession().setAttribute("user", user);
                auth = new UsernamePasswordAuthenticationToken("test3", "test3", getAuthorities(user));
            } else {
                UserEntity user = new UserEntity("b6fa75f0-8399-4869-9438-42da2baca30d", "Admin User", "adminuser@email.com");
                user.setSysadmin(true);
                user.setRoom(1820);
                users.save(user);
                auth = new UsernamePasswordAuthenticationToken("test3", "test3", getAuthorities(user));
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
    
}
