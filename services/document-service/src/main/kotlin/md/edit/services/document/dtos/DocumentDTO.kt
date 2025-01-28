package md.edit.services.document.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import md.edit.services.document.data.Document
import md.edit.services.document.data.DocumentVisibility
import java.util.*

class DocumentDTO(document: Document) {
    val id: UUID? = document.id

    val title: String = document.title

    val owner: UUID = document.owner
    val visibility: DocumentVisibility = document.visibility

    val lastModified: Date = document.data!!.lastModified
    val created: Date = document.created

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var content: String? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var revision: ULong? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var shared: MutableList<DocumentUserDTO>? = null
}

fun Document.toDTO(withShared: Boolean = false, withContent: Boolean = false): DocumentDTO {
    val documentDto = DocumentDTO(this)

    if (withShared) {
        documentDto.shared = this.documentUsers.map { it.toDTO() }.toMutableList()
    }

    if (withContent) {
        documentDto.content = this.data!!.content
        documentDto.revision = this.data!!.revision
    }

    return documentDto
}