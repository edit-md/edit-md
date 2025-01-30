package md.edit.services.file.dtos

import java.util.*

data class PresignedUploadURLDtoIn(val document: UUID, val name: String, val type: String)
