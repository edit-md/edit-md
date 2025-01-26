package md.edit.services.document.configuration.cookieauth

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component

@Component
class CookieAuthenticationFilter(
    private val cookieAuthenticationService: CookieAuthenticationService
) : Filter {

    @Value("\${edit-md.session.cookie.name}")
    private lateinit var sessionCookieName: String

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        if (request !is HttpServletRequest || response !is HttpServletResponse) {
            chain.doFilter(request, response)
            return
        }

        val authCookie = getAuthCookie(request)

        if (authCookie != null) {
            val token = authCookie.value
            try {
                val user = cookieAuthenticationService.fetchUserData(token) ?: throw Exception("User not found")

                val userDetails = CustomUserDetails(user)

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, null
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }

                SecurityContextHolder.getContext().authentication = authentication

            } catch (e: Exception) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.contentType = "application/json"
                response.writer.write("{\"error\":\"Authentication failed.\"}")
                response.writer.flush()
                return
            }
        }

        chain.doFilter(request, response)
    }

    private fun getAuthCookie(request: HttpServletRequest): Cookie? {
        return request.cookies?.firstOrNull { it.name == sessionCookieName }
    }
}

