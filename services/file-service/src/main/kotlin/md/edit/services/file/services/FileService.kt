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
import md.edit.services.file.dtos.FileDtoOut
import md.edit.services.file.repos.FileMetadataRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@Service
class FileService(private val fileRepository: FileRepository,
    private val metadataRepository: FileMetadataRepository) {

    fun getFileInformation(fileId: UUID): FileDtoOut {
        val file = metadataRepository.findById(fileId)
            .orElseThrow { IllegalArgumentException("File with ID $fileId not found") }
        return FileDtoOut(file.documentId, file.path, file.type, file.createdDate)
    }

    fun generatePresignedDownloadUrl(fileId: UUID): String {
        val file = metadataRepository.findById(fileId)
            .orElseThrow { IllegalArgumentException("File with ID $fileId not found") }
        return fileRepository.generatePresignedDownloadUrl(file.path)
    }

    fun getAllFilesFromDocument(documentId: UUID): List<FileDtoOut> {
        val files: List<File> = metadataRepository.findByDocumentId(documentId)
        val fileDtoList: MutableList<FileDtoOut> = ArrayList()
        for (file in files) {
            fileDtoList.add(FileDtoOut(file))
        }
        return fileDtoList
    }

    fun generatePresignedUploadUrl(): String {
        return fileRepository.generatePresignedUploadUrl()
    }

    fun deleteFile(fileId: UUID){
        val file = metadataRepository.findById(fileId)
            .orElseThrow { IllegalArgumentException("File with ID $fileId not found") }
        fileRepository.deleteFile(file.path)
        metadataRepository.deleteById(fileId)
    }
}
