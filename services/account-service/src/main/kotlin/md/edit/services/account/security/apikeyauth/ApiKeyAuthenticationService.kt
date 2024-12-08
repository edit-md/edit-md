package md.edit.services.account.security.apikeyauth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.stereotype.Service

@Service
class ApiKeyAuthenticationService {

    companion object {
        private const val AUTH_TOKEN_HEADER_NAME: String = "X-API-KEY"
    }

    @Value("\${edit-md.api-key}")
    private lateinit var authToken: String

    fun getAuthentication(request: HttpServletRequest): Authentication {
        val apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME)
        if (apiKey == null || apiKey != authToken) {
            throw BadCredentialsException("Invalid API Key")
        }

        return ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES)
    }

}