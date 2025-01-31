package md.edit.services.account.configuration.csrf

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.DefaultCsrfToken

// This filter checks for the presence of a CSRF Header in the request and writes a 403 response if the header is missing
// This is only working when CORS is set up correctly and the frontend is sending the CSRF header
// See https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#employing-custom-request-headers-for-ajaxapi for more information
class CsrfFilter(private val contextPath: String): Filter {
    private val csrfHeader = "X-CSRF-Protection"
    private val allowedRoutes = setOf("/oauth2", "/error", "/actuator")

    override fun init(filterConfig: FilterConfig?) {
        super.init(filterConfig)
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        // Allow preflight requests
        if(httpRequest.method == "OPTIONS") {
            chain.doFilter(httpRequest, httpResponse)
            return
        }

        // Allow requests to the OAuth2 and default error endpoints
        if(allowedRoutes.any { httpRequest.requestURI.replace(contextPath, "").startsWith(it) }) {
            chain.doFilter(httpRequest, httpResponse)
            return
        }

        // Allow requests with X-API-KEY header
        if(httpRequest.getHeader("X-API-KEY") != null) {
            chain.doFilter(httpRequest, httpResponse)
            return
        }

        val csrfValue = httpRequest.getHeader(csrfHeader)

        // Check if the CSRF header is present and if it is not, return a 403 response
        if (csrfValue == null || csrfValue.isEmpty()) {
            httpResponse.status = HttpStatus.FORBIDDEN.value()
            httpResponse.contentType = "application/json"
            httpResponse.writer.write("{\"error\":\"Missing CSRF Header\"}")
            httpResponse.writer.flush()
            return
        }

        val token: CsrfToken = DefaultCsrfToken(csrfHeader, csrfHeader, csrfValue)
        httpRequest.setAttribute(CsrfToken::class.java.getName(), token)

        chain.doFilter(httpRequest, httpResponse)
    }
}