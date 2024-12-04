package md.edit.services.account.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Value("\${edit-md.frontend.host}")
    private lateinit var applicationHost: String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        // Add the CSRF filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(CsrfFilter(), UsernamePasswordAuthenticationFilter::class.java)

        // Disable the default CSRF protection
        http.csrf { it.disable() }

        // Enable CORS and allow requests from the frontend
        http.cors {
            val source = UrlBasedCorsConfigurationSource()
            val config = CorsConfiguration()
            config.allowedOrigins = listOf(applicationHost)
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

        http.oauth2Login {
            it.loginPage("/") // This disables the automatic redirect to the authorization server
            it.defaultSuccessUrl("${applicationHost}/dashboard", true)
        }

        http.exceptionHandling {
            it.authenticationEntryPoint(CustomAuthenticationEntryPoint())
        }

        return http.build()
    }
}