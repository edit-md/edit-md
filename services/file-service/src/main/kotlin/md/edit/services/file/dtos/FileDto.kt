package md.edit.services.file.dtos

import java.time.LocalDateTime
import java.util.*

@JvmRecord
data class FileDto(val documentId: UUID, val path: String, val type: String, val createdDate: LocalDateTime)
