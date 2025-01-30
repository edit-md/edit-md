package md.edit.services.file.dtos

import md.edit.services.file.data.File
import java.time.LocalDateTime
import java.util.*

data class FileDtoOut(val id: UUID, val documentId: UUID, val name: String, val path: String, val type: String, val createdDate: LocalDateTime, val uploaded: Boolean, val fileSize: Long?){
    constructor(file: File) : this(file.id, file.documentId, file.name, file.path, file.type, file.createdDate, file.uploaded, file.fileSize)
}
