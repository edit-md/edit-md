package md.edit.services.document.security.cookieauth

import java.util.*

data class UserDataDTO (
    val id: UUID,
    val username: String,
    val email: String,
    val avatar: String
)