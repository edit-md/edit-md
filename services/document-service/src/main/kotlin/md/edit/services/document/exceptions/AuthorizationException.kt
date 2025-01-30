package md.edit.services.document.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You are not authorized to perform this action")
class AuthorizationException(override var message: String): RuntimeException(message) {
    constructor(): this("You are not authorized to perform this action")
}