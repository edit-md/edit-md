package md.edit.services.file.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import md.edit.services.file.data.DocumentPermission
import java.util.UUID

data class DocumentUserDto(

    @JsonProperty("id")
    val userId: UUID,
    val permission: DocumentPermission
)