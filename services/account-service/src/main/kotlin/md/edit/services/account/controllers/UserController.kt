package md.edit.services.account.controllers

import md.edit.services.account.dtos.UserDTO
import md.edit.services.account.security.apikeyauth.ApiKeyAuthentication
import md.edit.services.account.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("users")
class UserController(private val userService: UserService) {

    @GetMapping("/")
    fun getUser(@AuthenticationPrincipal principal: OAuth2User): Map<String, Any> {
        return principal.attributes
    }

    @GetMapping("/{id}")
    fun getUserById(authentication: Authentication, @PathVariable id: String): ResponseEntity<UserDTO> {
        val uuid = runCatching { UUID.fromString(id) }.getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        val user = userService.getUserById(uuid) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        if (authentication is ApiKeyAuthentication)
            return ResponseEntity.ok(UserDTO.fromUserWithConnectedAccounts(user))

        return ResponseEntity.ok(UserDTO.fromUserPrivate(user))
    }

    @GetMapping("me")
    fun getMe(authentication: Authentication): ResponseEntity<UserDTO> {
        val principal = authentication.principal

        if (principal !is OAuth2User)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "aaa")

        return ResponseEntity.ok(
            UserDTO.fromUserWithConnectedAccounts(
                userService.getUser(principal) ?: throw RuntimeException("User not found")
            )
        )
    }

    @GetMapping("mail")
    fun getUserMail(authentication: Authentication): String {
        val user = authentication.principal as OAuth2User
        return user.attributes["email"] as String
    }

    @GetMapping("debug")
    fun debugSecurityContext(): Any {
        return SecurityContextHolder.getContext().authentication ?: "No Authentication Found"
    }

}