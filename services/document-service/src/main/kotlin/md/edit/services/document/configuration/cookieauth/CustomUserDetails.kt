package md.edit.services.document.configuration.cookieauth

import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class CustomUserDetails(
    val id: UUID,
    private val username: String,
    val email: String,
    val avatar: String
) : UserDetails {

    constructor(user: UserDataDTO) : this(user.id, user.name, user.email, user.avatar)

    override fun getAuthorities() = null

    override fun getPassword() = ""

    override fun getUsername() = username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}