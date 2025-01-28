package md.edit.services.document.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Argument of the request is invalid")
class InvalidArgumentException(override var message: String): RuntimeException(message) {
}