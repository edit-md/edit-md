package md.edit.services.account.configuration.oauth

import md.edit.services.account.data.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import java.io.Serializable
import java.util.*

class CustomOAuth2User(
    val id: UUID,
    val email: String,
    val avatar: String?,
    private val delegate: OAuth2User
) : OAuth2User, Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    constructor(user: User, delegate: OAuth2User) : this(user.id!!, user.email, user.avatar, delegate)

    override fun getName(): String {
        return delegate.name
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return delegate.attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return delegate.authorities
    }

}