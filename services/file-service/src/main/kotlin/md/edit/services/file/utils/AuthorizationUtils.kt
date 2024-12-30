package md.edit.services.file.utils

import md.edit.services.document.configuration.apikeyauth.ApiKeyAuthentication
import md.edit.services.document.configuration.cookieauth.CustomUserDetails
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.server.ResponseStatusException
import java.util.*

class AuthorizationUtils {

    companion object {

        fun onlyAPI(auth: Authentication): ApiKeyAuthentication {
            if(auth !is ApiKeyAuthentication) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "This endpoint is only available for API requests")
            }

            return auth
        }

        fun onlyUser(auth: Authentication): CustomUserDetails {
            val principal = auth.principal

            if(principal !is CustomUserDetails) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "This endpoint is only available for authenticated users")
            }

            return principal
        }

        fun onlyUser(auth: Authentication, id: String): CustomUserDetails {
            val user = onlyUser(auth)
            val uuid = runCatching { UUID.fromString(id) }.getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id") }

            if(user.id != uuid) {
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resource")
            }

            return user
        }

    }

}