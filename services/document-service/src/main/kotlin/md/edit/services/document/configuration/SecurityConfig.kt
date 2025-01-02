package md.edit.services.document.configuration

import md.edit.services.document.configuration.apikeyauth.ApiKeyAuthenticationFilter
import md.edit.services.document.configuration.cookieauth.CookieAuthenticationFilter
import md.edit.services.document.configuration.csrf.CsrfFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val apiAuthenticationFilter: ApiKeyAuthenticationFilter,
    private val cookieAuthenticationFilter: CookieAuthenticationFilter
) {

    @Value("\${edit-md.domain}")
    private lateinit var applicationHost: String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        // Add the CSRF filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(CsrfFilter(), UsernamePasswordAuthenticationFilter::class.java)

        // Add the API authentication filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(apiAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        // Add the CookieAuthenticationFilter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(cookieAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        // Disable the default CSRF protection
        http.csrf { it.disable() }

        // Remove the protocol from the host
        val host = applicationHost.replace("https://", "").replace("http://", "")

        // Enable CORS and allow requests from the frontend
        http.cors {
            val source = UrlBasedCorsConfigurationSource()
            val config = CorsConfiguration()
            config.allowedOrigins = listOf("https://$host", "http://$host")
            config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            config.allowedHeaders = listOf("*")
            config.allowCredentials = true
            source.registerCorsConfiguration("/**", config)

            it.configurationSource(source)
        }

        http.authorizeHttpRequests {
            it.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            it.anyRequest().authenticated()
        }

        http.exceptionHandling {
            it.authenticationEntryPoint(CustomAuthenticationEntryPoint())
        }

        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        http.httpBasic{ it.disable() }
        http.formLogin{ it.disable() }
        http.logout{ it.disable() }

        return http.build()
    }
}