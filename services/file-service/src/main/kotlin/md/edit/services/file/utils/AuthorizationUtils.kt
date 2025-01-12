package md.edit.services.file.utils

import md.edit.services.file.configuration.apikeyauth.ApiKeyAuthentication
import md.edit.services.file.configuration.cookieauth.CustomUserDetails
import md.edit.services.file.exceptions.AuthorizationException
import org.springframework.security.core.Authentication
import java.util.*

class AuthorizationUtils {

    companion object {

        fun onlyAPI(auth: Authentication): ApiKeyAuthentication {
            if(auth !is ApiKeyAuthentication) {
                throw AuthorizationException()
            }

            return auth
        }

        fun onlyUser(auth: Authentication): CustomUserDetails {
            val principal = auth.principal

            if(principal !is CustomUserDetails) {
                throw AuthorizationException()
            }

            return principal
        }

        fun onlyUser(auth: Authentication, uuid: UUID): CustomUserDetails {
            val user = onlyUser(auth)

            if(user.id != uuid) {
                throw AuthorizationException()
            }

            return user
        }

        fun onlyUsers(auth: Authentication, vararg uuids: UUID): CustomUserDetails {
            val user = onlyUser(auth)

            if(user.id !in uuids) {
                throw AuthorizationException()
            }

            return user
        }

        fun isAPI(auth: Authentication): Boolean {
            return auth is ApiKeyAuthentication
        }

    }

}