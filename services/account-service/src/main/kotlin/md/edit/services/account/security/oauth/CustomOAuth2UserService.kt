package md.edit.services.account.security.oauth

import md.edit.services.account.services.GitHubService
import md.edit.services.account.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

@Component
class CustomOAuth2UserService(
    @Autowired private val gitHubService: GitHubService,
    @Autowired private val userService: UserService
) : DefaultOAuth2UserService() {

    @Override
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = CustomOAuth2User(super.loadUser(userRequest))

        user.addAttribute("accountOrigin", userRequest.clientRegistration.registrationId)

        // Fetch the user's primary email from GitHub because it is not provided by the OAuth2 provider
        if (userRequest.clientRegistration.registrationId == "github") {
            val email = gitHubService.getPrimaryEmail(userRequest.accessToken.tokenValue)
            user.addAttribute("email", email)
            user.addAttribute("avatar", user.attributes["avatar_url"] ?: "")
        }

        // Save the user to the database
        println("LocalUser: ${userService.getAndUpdateOrCreateUser(user)}")

        println("User: ${user.attributes}")

        return user;
    }
}