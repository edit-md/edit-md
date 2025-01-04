package md.edit.services.file.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import md.edit.services.file.data.DocumentVisibility
import java.util.*

data class DocumentDataDto(

    val id: UUID,

    val title: String,

    val owner: UUID,
    val visibility: DocumentVisibility,

    val lastModified: Date,
    val created: Date,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var content: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var shared: MutableList<DocumentUserDTO>? = null
)
