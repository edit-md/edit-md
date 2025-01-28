package md.edit.services.file.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Requested file ist not an image")
class NotAnImageException: RuntimeException() {
    override var message: String = "Requested file ist not an image"
}