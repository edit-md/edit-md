package md.edit.services.file.dtos

import md.edit.services.file.data.File

data class PresignedDownloadURLDtoOut(val file: FileDtoOut, val url: String){
    constructor(file: File, presignedUrl: String) : this(FileDtoOut(file), presignedUrl)
}
