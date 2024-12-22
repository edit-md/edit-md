package md.edit.services.account.security.oauth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import java.io.Serializable

class CustomOAuth2User(
    private val delegate: OAuth2User,
    private val additionalAttributes: MutableMap<String, Any> = mutableMapOf()
) : OAuth2User, Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? = delegate.authorities
    override fun getName(): String = delegate.name

    override fun getAttributes(): MutableMap<String, Any> {
        // Combine original attributes with additional ones
        return mutableMapOf<String, Any>().apply {
            putAll(delegate.attributes)
            putAll(additionalAttributes)
        }
    }

    fun addAttribute(key: String, value: Any) {
        additionalAttributes[key] = value
    }
}