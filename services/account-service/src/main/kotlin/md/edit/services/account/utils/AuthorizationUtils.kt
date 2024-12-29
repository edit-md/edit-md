package md.edit.services.account.utils

import md.edit.services.account.security.apikeyauth.ApiKeyAuthentication
import md.edit.services.account.security.oauth.CustomOAuth2User
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.server.ResponseStatusException

class AuthorizationUtils {

    companion object {

        fun onlyAPI(auth: Authentication): ApiKeyAuthentication {
            if(auth !is ApiKeyAuthentication) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "This endpoint is only available for API requests")
            }

            return auth
        }

        fun onlyUser(auth: Authentication): CustomOAuth2User {
            val principal = auth.principal

            if(principal !is CustomOAuth2User) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "This endpoint is only available for authenticated users")
            }

            return principal
        }

    }

}