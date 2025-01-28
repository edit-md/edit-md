package md.edit.services.file.configuration.cookieauth

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CookieAuthenticationService(private val restTemplate: RestTemplate) {

    @Value("\${edit-md.account-service.host}")
    private lateinit var accountServiceHost: String

    @Value("\${edit-md.session.cookie.name}")
    private lateinit var sessionCookieName: String

    fun fetchUserData(sessionToken: String): UserDataDTO? {
        val url = "$accountServiceHost/api/accounts/me"
        val cookies = mapOf(sessionCookieName to sessionToken)

        val headers = HttpHeaders().apply {
            add("Cookie", cookies.entries.joinToString("; ") { "${it.key}=${it.value}" })
            add("X-CSRF-Protection", "1")
        }

        val entity = HttpEntity<Any>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, UserDataDTO::class.java)

        return response.body
    }

}