package md.edit.services.document.dtos

import md.edit.services.document.data.DocumentVisibility

data class DocumentInDTO(
    val title: String,
    val visibility: DocumentVisibility? = null,
    val shared: List<DocumentUserInDTO>? = null
)