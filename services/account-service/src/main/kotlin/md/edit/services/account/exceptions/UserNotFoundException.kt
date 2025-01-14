package md.edit.services.account.exceptions

import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not Found")
class UserNotFoundException: ChangeSetPersister.NotFoundException() {
    override var message: String = "User not found"
}