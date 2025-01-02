package md.edit.services.document.dtos

import md.edit.services.document.data.DocumentVisibility
import md.edit.services.document.validation.NullOrNotBlank

data class DocumentPatchDTO(
    @field:NullOrNotBlank(message = "Title is required")
    val title: String? = null,
    val visibility: DocumentVisibility? = null
)