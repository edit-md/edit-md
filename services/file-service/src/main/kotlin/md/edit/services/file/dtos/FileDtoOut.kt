package md.edit.services.file.dtos

import md.edit.services.file.data.File
import java.time.LocalDateTime
import java.util.*

data class FileDtoOut(val documentId: UUID, val path: String, val type: String, val createdDate: LocalDateTime){
    constructor(file: File) : this(file.documentId, file.path, file.type, file.createdDate)
}
