package md.edit.services.document.utils

import md.edit.services.document.configuration.apikeyauth.ApiKeyAuthentication
import md.edit.services.document.configuration.cookieauth.CustomUserDetails
import md.edit.services.document.exceptions.AuthorizationException
import org.springframework.security.core.Authentication
import java.util.*

class AuthorizationUtils {

    companion object {

        fun onlyAPI(auth: Authentication): ApiKeyAuthentication {
            if(auth !is ApiKeyAuthentication) {
                throw AuthorizationException("Not an API key")
            }

            return auth
        }

        fun onlyUser(auth: Authentication?): CustomUserDetails {
            if(auth == null) {
                throw AuthorizationException("Not authenticated")
            }

            val principal = auth.principal

            if(principal !is CustomUserDetails) {
                throw AuthorizationException("Not a user")
            }

            return principal
        }

        fun onlyUser(auth: Authentication?, uuid: UUID): CustomUserDetails {
            val user = onlyUser(auth)

            if(user.id != uuid) {
                throw AuthorizationException("Not authorized")
            }

            return user
        }

        fun onlyUsers(auth: Authentication?, vararg uuids: UUID): CustomUserDetails {
            val user = onlyUser(auth)

            if(user.id !in uuids) {
                throw AuthorizationException("Not authorized")
            }

            return user
        }

        fun isAPI(auth: Authentication?): Boolean {
            return auth is ApiKeyAuthentication
        }

        fun isUser(auth: Authentication?, vararg uuids: UUID): Boolean {
            if(auth == null) {
                return false
            }

            val principal = auth.principal
            return principal is CustomUserDetails && principal.id in uuids
        }

    }

}