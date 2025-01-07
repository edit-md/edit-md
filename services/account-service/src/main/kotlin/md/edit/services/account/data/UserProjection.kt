package md.edit.services.account.data

import java.util.*

interface UserProjection {
    val id: UUID
    val name: String
    val email: String
    val avatar: String?
}