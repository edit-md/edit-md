package md.edit.services.document.dtos

import jakarta.validation.constraints.NotBlank
import md.edit.services.document.data.DocumentVisibility

data class DocumentCreateDTO(
    @field:NotBlank(message = "Title is required")
    val title: String,
    val visibility: DocumentVisibility? = null,
    val shared: List<DocumentUserInDTO>? = null
)