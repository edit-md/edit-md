package md.edit.services.document.dtos

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class DocumentChangeInsertDTO(
    @param:JsonProperty("r") @get:JsonProperty("r")
    var revision: ULong,

    @param:JsonProperty("i") @get:JsonProperty("i")
    var index: ULong,

    @param:JsonProperty("c") @get:JsonProperty("c")
    var content: String
)