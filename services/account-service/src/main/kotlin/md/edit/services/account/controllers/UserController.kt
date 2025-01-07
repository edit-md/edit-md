package md.edit.services.account.controllers

import md.edit.services.account.data.UserSettings
import md.edit.services.account.dtos.UserDTO
import md.edit.services.account.dtos.toDTO
import md.edit.services.account.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class UserController(private val userService: UserService) {

    @GetMapping("/")
    fun getUser(@AuthenticationPrincipal principal: OAuth2User): Map<String, Any> {
        return principal.attributes
    }

    @GetMapping("/{id}")
    fun getUserById(authentication: Authentication, @PathVariable id: UUID): ResponseEntity<UserDTO> {
        val user = userService.getUserById(authentication, id)

        return ResponseEntity.ok(
            user.toDTO(
                withConnectedAccounts = true,
                withSettings = true,
                withEmail = true
            )
        )
    }

    @GetMapping("me")
    fun getMe(authentication: Authentication): ResponseEntity<UserDTO> {
        val user = userService.getUser(authentication)

        return ResponseEntity.ok(
            user.toDTO(
                withConnectedAccounts = true,
                withSettings = true,
                withEmail = true
            )
        )
    }

    @GetMapping("/search")
    fun getUsersByName(
        authentication: Authentication, @RequestParam name: String
    ): ResponseEntity<Collection<UserDTO>> {
        return ResponseEntity.ok(userService.searchNames(authentication, name).map { it.toDTO() }.toList())
    }

    @GetMapping("me/settings")
    fun getUserSettings(authentication: Authentication): ResponseEntity<UserSettings> {
        val user = userService.getUser(authentication)

        return ResponseEntity.ok(user.settings)
    }

    @PatchMapping("me/settings")
    fun updateUserSettings(
        authentication: Authentication, @RequestBody userSettingsDTO: UserSettings
    ): ResponseEntity<UserSettings> {
        val user = userService.getUser(authentication)
        val updatedUser = userService.updateUserSettings(user, userSettingsDTO)
        return ResponseEntity.ok(updatedUser.settings)
    }
}