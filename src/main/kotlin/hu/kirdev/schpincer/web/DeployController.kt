package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class DeployController {

    @Autowired
    private lateinit var users: UserService

    @Value("\${schpincer.sysadmins:}")
    private lateinit var systemAdmins: String

    @GetMapping("/install/grant-basics")
    fun grantBasicPermissions(): String {
        return systemAdmins.split(",")
                .filter { it.isNotEmpty() }
                .map { users.grantAdmin(it) }
                .joinToString { "," }
    }

}
