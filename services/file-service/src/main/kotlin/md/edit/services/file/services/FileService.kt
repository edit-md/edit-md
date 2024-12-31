package md.edit.services.file.services

import md.edit.services.file.repos.FileRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import io.minio.errors.MinioException
import md.edit.services.file.data.File
import md.edit.services.file.repos.FileMetadataRepository
import java.time.LocalDateTime
import java.util.*

@Service
class FileService(private val fileRepository: FileRepository,
    private val metadataRepository: FileMetadataRepository) {

    @Throws(IOException::class, MinioException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun uploadFile(file: MultipartFile): String {
        val documentId = UUID.randomUUID()
        val path = "files/${documentId}/${file.originalFilename}"
        fileRepository.uploadFile(file, path)

        val metadata = File(
            documentId = documentId,
            path = path,
            createdDate = LocalDateTime.now(),
            type = file.contentType ?: "application/octet-stream"
        )
        metadataRepository.save(metadata)
        return path
    }

    @Throws(MinioException::class, IOException::class)
    fun downloadFile(objectName: String): InputStream {
        return fileRepository.downloadFile(objectName)
    }

    @Throws(MinioException::class, IOException::class, IllegalArgumentException::class)
    fun generatePresidedDownloadUrl(fileId: UUID): String {
        val file = metadataRepository.findById(fileId)
            .orElseThrow { IllegalArgumentException("File with ID $fileId not found") }
        return fileRepository.generatePresidedDownloadUrl(file.path)
    }
}
