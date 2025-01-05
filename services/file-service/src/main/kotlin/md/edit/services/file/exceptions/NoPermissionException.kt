package md.edit.services.file.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You do not have the right permission on this document")
class NoPermissionException: RuntimeException() {
    override var message: String = "You do not have the right permission on this document"
}