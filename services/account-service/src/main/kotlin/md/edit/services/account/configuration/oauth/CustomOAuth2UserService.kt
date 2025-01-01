package md.edit.services.account.configuration.oauth

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
    override fun loadUser(originalUserRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(originalUserRequest)

        val userRequest = CustomOAuth2UserRequest(
            name = oauth2User.attributes["name"].toString(),
            email = oauth2User.attributes["email"].toString(),
            avatar = oauth2User.attributes["avatar_url"].toString(),
            remoteId = oauth2User.attributes["id"].toString(),
            accountOrigin = originalUserRequest.clientRegistration.registrationId
        )

        // Fetch the user's primary email from GitHub because it is not provided by the OAuth2 provider
        if (userRequest.accountOrigin == "github") {
            userRequest.email = gitHubService.getPrimaryEmail(originalUserRequest.accessToken.tokenValue)
        }

        return CustomOAuth2User(userService.createOrUpdateAndGetUser(userRequest), oauth2User)
    }
}