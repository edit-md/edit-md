package md.edit.services.file.configuration.cookieauth

import java.util.*

data class UserDataDTO (
    val id: UUID,
    val name: String,
    val email: String,
    val avatar: String
)