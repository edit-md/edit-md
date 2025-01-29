package md.edit.services.file.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "File is already uploaded")
class FileAlreadyUploadedException: RuntimeException() {
    override var message: String = "File is already uploaded"
}