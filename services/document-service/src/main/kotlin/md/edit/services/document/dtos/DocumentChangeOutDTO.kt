package md.edit.services.document.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import md.edit.services.document.data.DocumentChange
import md.edit.services.document.data.DocumentChangeType

data class DocumentChangeOutDTO(

    @param:JsonProperty("t") @get:JsonProperty("t")
    val type: DocumentChangeType,

    @param:JsonProperty("r") @get:JsonProperty("r")
    val revision: ULong,

    @param:JsonProperty("i") @get:JsonProperty("i")
    val index: ULong,

    @param:JsonInclude(JsonInclude.Include.NON_NULL) @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("l") @get:JsonProperty("l")
    var length: ULong? = null,

    @param:JsonInclude(JsonInclude.Include.NON_NULL) @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("c") @get:JsonProperty("c")
    var content: String? = null,
) {
    constructor(documentChange: DocumentChange) : this(
        documentChange.changeType,
        documentChange.id.revision,
        documentChange.index
    )
}

fun DocumentChange.toDTO(): DocumentChangeOutDTO {
    val documentChangeDto = DocumentChangeOutDTO(this)

    if (this.changeType == DocumentChangeType.INSERT) {
        documentChangeDto.content = this.content
    }

    if (this.changeType == DocumentChangeType.DELETE) {
        documentChangeDto.length = this.length
    }

    return documentChangeDto
}