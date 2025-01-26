package md.edit.services.file.exceptions

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Uploaded file not found")
class UploadedFileNotFoundException: NotFoundException() {
    override val message: String = "Uploaded file not found"
}