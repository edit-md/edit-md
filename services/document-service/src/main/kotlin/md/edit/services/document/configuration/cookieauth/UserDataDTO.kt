package md.edit.services.document.configuration.cookieauth

import java.util.*

data class UserDataDTO (
    val id: UUID,
    val name: String,
    val email: String,
    val avatar: String
)