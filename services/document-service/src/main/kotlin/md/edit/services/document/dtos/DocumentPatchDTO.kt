package md.edit.services.document.dtos

import md.edit.services.document.data.DocumentVisibility

data class DocumentPatchDTO(
    val title: String? = null,
    val visibility: DocumentVisibility? = null
)