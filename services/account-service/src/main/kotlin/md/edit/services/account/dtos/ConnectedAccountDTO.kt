package md.edit.services.account.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import md.edit.services.account.data.ConnectedAccount

class ConnectedAccountDTO private constructor(connectedAccount: ConnectedAccount) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var user: UserDTO? = null
        private set

    val provider: String = connectedAccount.id.provider
    val remoteId: String = connectedAccount.id.remoteId

    companion object {
        fun from(connectedAccount: ConnectedAccount): ConnectedAccountDTO {
            return ConnectedAccountDTO(connectedAccount)
        }

        fun fromConnectedAccountWithUser(connectedAccount: ConnectedAccount): ConnectedAccountDTO {
            val connectedAccountDto = ConnectedAccountDTO(connectedAccount)
            connectedAccountDto.user = UserDTO.from(connectedAccount.user)
            return connectedAccountDto
        }
    }
}