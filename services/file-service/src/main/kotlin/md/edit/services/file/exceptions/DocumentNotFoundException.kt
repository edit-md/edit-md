package md.edit.services.file.exceptions

import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Document not found")
class DocumentNotFoundException: ChangeSetPersister.NotFoundException() {
    override var message: String = "Document not found"
}