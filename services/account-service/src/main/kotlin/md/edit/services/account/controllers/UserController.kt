package md.edit.services.account.controllers

import md.edit.services.account.configuration.apikeyauth.ApiKeyAuthentication
import md.edit.services.account.data.Header
import md.edit.services.account.data.Theme
import md.edit.services.account.data.UserSettings
import md.edit.services.account.dtos.UserDTO
import md.edit.services.account.dtos.toDTO
import md.edit.services.account.services.UserService
import md.edit.services.account.utils.AuthorizationUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class UserController(private val userService: UserService) {

    @GetMapping("/")
    fun getUser(@AuthenticationPrincipal principal: OAuth2User): Map<String, Any> {
        return principal.attributes
    }

    @GetMapping("/{id}")
    fun getUserById(authentication: Authentication, @PathVariable id: UUID): ResponseEntity<UserDTO> {
        val user = userService.getUserById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        if (authentication is ApiKeyAuthentication)
            return ResponseEntity.ok(
                user.toDTO(
                    withConnectedAccounts = true,
                    withSettings = true,
                    withEmail = true
                )
            )
        return ResponseEntity.ok(user.toDTO())
    }

    @GetMapping("me")
    fun getMe(authentication: Authentication): ResponseEntity<UserDTO> {
        val user = AuthorizationUtils.onlyUser(authentication)

        return ResponseEntity.ok(userService.getUser(user)?.toDTO() ?: throw RuntimeException("User not found"))
    }

    @GetMapping("me/settings")
    fun getUserSettings(authentication: Authentication): ResponseEntity<UserSettings> {
        val user = AuthorizationUtils.onlyUser(authentication)

        return ResponseEntity.ok(userService.getUser(user)?.toDTO(
            withConnectedAccounts = false,
            withSettings = true,
            withEmail = false
        )?.settings)
    }

    @PatchMapping("me/settings")
    fun updateUserSettings(authentication: Authentication): ResponseEntity<UserSettings> {
        val user = AuthorizationUtils.onlyUser(authentication)

        // PATCH
        val oldUser = userService.getUser(user)

        oldUser?.settings = UserSettings(Theme.LIGHT, Header.WIDE)
        userService.updateUser(oldUser!!)

        // GET/RETURN
        //lazy af
        //return ResponseEntity.ok(getUserSettings(authentication).body)
        return ResponseEntity.ok(getMe(authentication).body?.settings)
    }
}