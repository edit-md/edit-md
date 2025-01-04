package md.edit.services.file.dtos

import md.edit.services.file.data.DocumentPermission
import java.util.UUID

data class DocumentUserDTO(
    val userId: UUID,
    val permission: DocumentPermission
)