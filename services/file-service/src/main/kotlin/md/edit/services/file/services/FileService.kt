package md.edit.services.file.services

import md.edit.services.file.repos.FileRepository
import org.springframework.stereotype.Service
import md.edit.services.file.data.File
import md.edit.services.file.dtos.DocumentDataDto
import md.edit.services.file.dtos.FileDtoOut
import md.edit.services.file.repos.FileMetadataRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.io.FileNotFoundException
import java.util.*
import kotlin.collections.ArrayList

@Service
class FileService(private val fileRepository: FileRepository,
                  private val metadataRepository: FileMetadataRepository,
                  private val restTemplate: RestTemplate) {

    @Value("\${edit-md.document-service.host}")
    private lateinit var documentServiceHost: String

    @Value("\${edit-md.api-key}")
    private lateinit var apiKey: String

    fun getFileInformation(fileId: UUID): FileDtoOut {
        val file = metadataRepository.findById(fileId)
            .orElseThrow { FileNotFoundException("File with ID $fileId not found") }
        return FileDtoOut(file)
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

    fun fetchDocumentData(id: UUID): DocumentDataDto? {
        val url = "$documentServiceHost/api/documents/$id"

        val headers = HttpHeaders().apply {
            add("X-CSRF-Protection", "1")
            add("X-API-KEY", apiKey)
        }

        val entity = HttpEntity<Any>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, DocumentDataDto::class.java)

        return response.body
    }
}
