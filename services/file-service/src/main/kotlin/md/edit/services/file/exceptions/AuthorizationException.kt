package md.edit.services.file.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You are not authorized to perform this action")
class AuthorizationException: RuntimeException() {
    override var message: String = "You are not authorized to perform this action"
}