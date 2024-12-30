package md.edit.services.document.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import md.edit.services.document.data.DocumentUser

class DocumentUserDTO(documentUser: DocumentUser) {

    @JsonProperty("id")
    val userId = documentUser.id.userId

    val permission = documentUser.permission

    companion object {
        fun from(documentUser: DocumentUser): DocumentUserDTO {
            return DocumentUserDTO(documentUser)
        }
    }
}