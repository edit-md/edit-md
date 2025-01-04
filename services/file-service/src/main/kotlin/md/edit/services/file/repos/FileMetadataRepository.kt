package md.edit.services.file.repos

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import md.edit.services.file.data.File
import md.edit.services.file.dtos.FileDtoOut

interface FileMetadataRepository : JpaRepository<File, UUID>{

    fun findByDocumentId(documentId: UUID): List<File>
}