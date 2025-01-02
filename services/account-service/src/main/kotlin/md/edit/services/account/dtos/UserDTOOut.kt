package md.edit.services.account.dtos

import md.edit.services.account.data.User
import java.util.*

class UserDTOOut constructor(user: User) {
    val name: String = user.name
    val avatar: String? = user.avatar
    val id: UUID? = user.id
}