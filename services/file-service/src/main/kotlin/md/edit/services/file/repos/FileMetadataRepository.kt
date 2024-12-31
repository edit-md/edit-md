package md.edit.services.file.repos

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import md.edit.services.file.data.File

interface FileMetadataRepository : JpaRepository<File, UUID>