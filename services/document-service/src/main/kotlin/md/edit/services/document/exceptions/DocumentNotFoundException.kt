package md.edit.services.document.exceptions

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Document not found")
class DocumentNotFoundException: NotFoundException() {
    override var message: String = "Document not found"
}