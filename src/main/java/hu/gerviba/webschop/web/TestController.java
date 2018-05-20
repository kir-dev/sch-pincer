package hu.gerviba.webschop.web;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Profile("test")
@RequestMapping("/test/")
public class TestController {

    @GetMapping("/login/simple")
    @ResponseBody
    public String loginSimpleUser() {
        return "OK";
    }

    @GetMapping("/login/circle")
    @ResponseBody
    public String loginCircleOwner() {
        return "OK";
    }

    @GetMapping("/login/admin")
    @ResponseBody
    public String loginAdmin() {
        return "OK";
    }
    
}
