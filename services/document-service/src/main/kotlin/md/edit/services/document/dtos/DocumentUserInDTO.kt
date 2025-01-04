package md.edit.services.document.dtos

import md.edit.services.document.data.DocumentPermission
import java.util.*

data class DocumentUserInDTO(
    val userId: UUID,
    val permission: DocumentPermission
)
