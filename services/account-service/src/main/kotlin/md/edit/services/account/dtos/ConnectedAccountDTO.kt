package md.edit.services.account.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import md.edit.services.account.data.ConnectedAccount

class ConnectedAccountDTO(connectedAccount: ConnectedAccount) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var user: UserDTO? = null

    val provider: String = connectedAccount.id.provider
    val remoteId: String = connectedAccount.id.remoteId
}

fun ConnectedAccount.toDTO(withUser: Boolean = false): ConnectedAccountDTO {
    val connectedAccountDto = ConnectedAccountDTO(this)

    if (withUser) {
        connectedAccountDto.user = this.user.toDTO()
    }

    return connectedAccountDto
}