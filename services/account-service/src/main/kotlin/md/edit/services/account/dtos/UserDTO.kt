package md.edit.services.account.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import md.edit.services.account.data.User
import md.edit.services.account.data.usersettings.UserSettings
import java.util.*

class UserDTO private constructor(user: User) {

    val id: UUID? = user.id

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var email: String? = user.email
        private set

    val name: String = user.name
    val avatar: String? = user.avatar
    var settings: UserSettings = user.settings

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var connectedAccounts: MutableList<ConnectedAccountDTO>? = null
        private set

    companion object {
        fun from(user: User): UserDTO {
            return UserDTO(user)
        }

        fun fromUserWithConnectedAccounts(user: User): UserDTO {
            val userDto = UserDTO(user)
            userDto.connectedAccounts = mutableListOf()
            user.connectedAccounts.forEach {
                userDto.connectedAccounts!!.add(ConnectedAccountDTO.from(it))
            }
            return userDto
        }

        fun fromUserPrivate(user: User): UserDTO {
            val userDto = UserDTO(user)
            userDto.email = null
            return userDto
        }
    }

}