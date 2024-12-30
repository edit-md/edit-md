package md.edit.services.document.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import md.edit.services.document.data.Document
import md.edit.services.document.data.DocumentVisibility
import java.util.*

class DocumentDTO(document: Document) {
    val id: UUID? = document.id

    val title: String = document.title
    val content: String = document.content

    val owner: UUID = document.owner
    val visibility: DocumentVisibility = document.visibility

    val lastModified: Date = document.lastModified
    val created: Date = document.created

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var shared: MutableList<DocumentUserDTO>? = null
        private set

    companion object {
        fun from(document: Document): DocumentDTO {
            val documentDto = DocumentDTO(document)
            return documentDto
        }

        fun fromDocumentWithShared(document: Document): DocumentDTO {
            val documentDto = DocumentDTO(document)
            documentDto.shared = document.documentUsers.map { DocumentUserDTO.from(it) }.toMutableList()
            return documentDto
        }
    }
}
