package md.edit.services.account.controllers

import org.kohsuke.github.GitHubBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("user")
class UserController @Autowired constructor(private val authorizedClientService: OAuth2AuthorizedClientService) {

    @GetMapping("/")
    fun getUser(@AuthenticationPrincipal principal: OAuth2User): Map<String, Any> {
        return principal.attributes
    }

    @GetMapping("mail")
    fun getUserMail(): String {
        println("Getting user mail")
        val authentication = SecurityContextHolder.getContext()
        val oauthToken = authentication.authentication as OAuth2AuthenticationToken
        val client = authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
            oauthToken.authorizedClientRegistrationId,
            oauthToken.name
        )

        if (oauthToken.authorizedClientRegistrationId == "github") {
            return GitHubBuilder().withOAuthToken(client?.accessToken?.tokenValue).build().myself.listEmails()
                .find { it.isPrimary }?.email ?: ""
        }

        return oauthToken.principal.attributes["email"] as String
    }

    @GetMapping("debug")
    fun debugSecurityContext(): Any {
        return SecurityContextHolder.getContext().authentication ?: "No Authentication Found"
    }

}