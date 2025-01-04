package md.edit.services.account.configuration

import md.edit.services.account.configuration.apikeyauth.ApiKeyAuthenticationFilter
import md.edit.services.account.configuration.csrf.CsrfFilter
import md.edit.services.account.configuration.oauth.CustomOAuth2UserService
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
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val apiAuthenticationFilter: ApiKeyAuthenticationFilter
) {

    @Value("\${edit-md.domain}")
    private lateinit var applicationHost: String

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        // Add the CSRF filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(CsrfFilter(), UsernamePasswordAuthenticationFilter::class.java)

        // Add the API authentication filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(apiAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

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

        http.oauth2Login {
            it.loginPage("/") // This disables the automatic redirect to the authorization server
            it.defaultSuccessUrl(applicationHost, true)
            it.userInfoEndpoint { userInfo ->
                userInfo.userService(customOAuth2UserService)
            }
        }

        http.exceptionHandling {
            it.authenticationEntryPoint(CustomAuthenticationEntryPoint())
        }

        return http.build()
    }
}