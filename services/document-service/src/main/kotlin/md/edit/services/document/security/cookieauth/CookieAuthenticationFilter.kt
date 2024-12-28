package md.edit.services.document.security.cookieauth
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.Cookie

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import java.util.UUID

@Component
class CookieAuthenticationFilter : Filter {

    companion object {
        private const val AUTH_COOKIE_NAME = "EDITMD_SESSION"
        private const val USER_INFO_SERVICE_URL = "http://user-service/api/userinfo"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        if(request !is HttpServletRequest || response !is HttpServletResponse) {
            chain.doFilter(request, response)
            return
        }

        val authCookie = getAuthCookie(request)

        println("Auth cookie: $authCookie")

        if (authCookie != null) {
            val token = authCookie.value
            try {
                val userResponse = fetchUserData(token)
                if (userResponse.statusCode == HttpStatus.OK && userResponse.body != null) {
                    val userData = userResponse.body!!

                    val userDetails = CustomUserDetails(userData)

                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, null
                    ).apply {
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }

                    SecurityContextHolder.getContext().authentication = authentication
                }
            } catch (e: Exception) {
                // Handle exception (e.g., log it, set response status)
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                return
            }
        }

        chain.doFilter(request, response)
    }

    private fun getAuthCookie(request: HttpServletRequest): Cookie? {
        return request.cookies?.firstOrNull { it.name == AUTH_COOKIE_NAME }
    }

    private fun fetchUserData(token: String): ResponseEntity<UserDataDTO> {
        val headers = HttpHeaders().apply {
            set(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }

        // ToDo: Replace with actual HTTP request
        return ResponseEntity.ok(UserDataDTO(
            id = UUID.randomUUID(),
            username = "user",
            email = "test@test.de",
            avatar = "https://example.com/avatar.png"))
    }
}

