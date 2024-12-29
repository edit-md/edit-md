package md.edit.services.account.configuration.apikeyauth

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class ApiKeyAuthenticationFilter(@Autowired private val apiKeyAuthenticationService: ApiKeyAuthenticationService) :
    Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        try {
            if (request !is HttpServletRequest)
                throw IllegalArgumentException("Request and Response must be of type HttpServletRequest and HttpServletResponse")

            if (request.getHeader("X-API-KEY") == null) {
                filterChain.doFilter(request, response)
                return
            }

            val authentication: Authentication = apiKeyAuthenticationService.getAuthentication(request)
            SecurityContextHolder.getContext().authentication = authentication
        } catch (exp: Exception) {
            val httpResponse = response as HttpServletResponse
            httpResponse.status = HttpStatus.UNAUTHORIZED.value()
            httpResponse.contentType = "application/json"
            httpResponse.writer.write("{\"error\":\"${exp.message}\"}")
            httpResponse.writer.flush()
            httpResponse.writer.close()
            return
        }

        filterChain.doFilter(request, response)
    }
}