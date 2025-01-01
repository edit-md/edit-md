package md.edit.services.account.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import md.edit.services.account.data.User
import md.edit.services.account.data.UserSettings
import java.util.*

class UserDTO(user: User) {

    val id: UUID? = user.id

    val name: String = user.name
    val avatar: String? = user.avatar

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var email: String? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var settings: UserSettings? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var connectedAccounts: MutableList<ConnectedAccountDTO>? = null

}

//all options
fun User.toDTO(): UserDTO {
    return this.toDTO(withConnectedAccounts = true, withSettings = true, withEmail = true)
}

//choose what options you want
fun User.toDTO(withConnectedAccounts: Boolean = false, withSettings: Boolean = false, withEmail: Boolean = false): UserDTO {
    val userDto = UserDTO(this)

    if (withConnectedAccounts) {
        userDto.connectedAccounts = this.connectedAccounts.map { it.toDTO() }.toMutableList()
    }

    if (withSettings) {
        userDto.settings = UserSettings()
    }

    if (withEmail) {
        userDto.email = this.email
    }

    return userDto
}