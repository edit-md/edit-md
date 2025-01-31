package md.edit.services.document.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
class UserNotFoundException: RuntimeException() {
    override var message: String = "User not found"
}