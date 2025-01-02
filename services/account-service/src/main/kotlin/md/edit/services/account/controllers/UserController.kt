package md.edit.services.account.controllers

import md.edit.services.account.configuration.apikeyauth.ApiKeyAuthentication
import md.edit.services.account.data.UserSettings
import md.edit.services.account.dtos.UserDTO
import md.edit.services.account.dtos.UserSettingsDTO
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
        val authUser = AuthorizationUtils.onlyUser(authentication)

        return ResponseEntity.ok(
            userService.getUser(authUser)?.toDTO(
                withConnectedAccounts = true,
                withSettings = true,
                withEmail = true
            ) ?: throw RuntimeException("User not found")
        )
    }

    @GetMapping("me/settings")
    fun getUserSettings(authentication: Authentication): ResponseEntity<UserSettings> {
        val authUser = AuthorizationUtils.onlyUser(authentication)

        return ResponseEntity.ok(userService.getUser(authUser)?.settings)
    }

    @PatchMapping("me/settings")
    fun updateUserSettings(authentication: Authentication, @RequestBody userSettingsDTO: UserSettingsDTO): ResponseEntity<UserSettings> {
        val authUser = AuthorizationUtils.onlyUser(authentication)
        //for testing
        //val userSettings = UserSettings(Theme.LIGHT,Header.WIDE)

        // PATCH
        val user = userService.getUser(authUser) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        val updatedUser = userService.updateUserSettings(user, userSettingsDTO)

        // GET/RETURN
        return ResponseEntity.ok(updatedUser.settings)
    }
}