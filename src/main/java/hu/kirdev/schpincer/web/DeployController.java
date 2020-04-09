package hu.kirdev.schpincer.web;

import hu.kirdev.schpincer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployController {

	@Autowired
	private UserService users;
	
	@GetMapping("/install/grant-basics")
	public String grantBasicPermissions() {
		return "" + users.grantAdmin("6af3c8b0-592c-6864-8dd9-5f354edfc0be");
	}
	
}
