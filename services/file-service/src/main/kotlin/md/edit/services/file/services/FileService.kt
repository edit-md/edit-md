package md.edit.services.file.services

import md.edit.services.file.configuration.cookieauth.CustomUserDetails
import md.edit.services.file.data.DocumentPermission
import md.edit.services.file.data.DocumentVisibility
import md.edit.services.file.repos.FileRepository
import org.springframework.stereotype.Service
import md.edit.services.file.data.File
import md.edit.services.file.dtos.DocumentDataDto
import md.edit.services.file.dtos.DocumentUserDto
import md.edit.services.file.dtos.FileDtoOut
import md.edit.services.file.exceptions.DocumentNotFoundException
import md.edit.services.file.exceptions.NoPermissionException
import md.edit.services.file.exceptions.UploadedFileNotFoundException
import md.edit.services.file.repos.FileMetadataRepository
import md.edit.services.file.utils.AuthorizationUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.core.Authentication
import org.springframework.web.client.RestTemplate
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
            .orElseThrow { UploadedFileNotFoundException() }
        return FileDtoOut(file)
    }

    fun generatePresignedDownloadUrl(fileId: UUID, authentication: Authentication): String {
        val user = AuthorizationUtils.onlyUser(authentication)

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(!checkIfUserHasPermissionOnDocument(user, document, DocumentPermission.READ) && document.visibility != DocumentVisibility.PUBLIC) {
            throw NoPermissionException()
        }

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

    fun generatePresignedUploadUrl(documentId: UUID, authentication: Authentication): String {
        val user = AuthorizationUtils.onlyUser(authentication)

        val document = fetchDocumentData(documentId) ?: throw DocumentNotFoundException()

        if(!checkIfUserHasPermissionOnDocument(user, document, DocumentPermission.WRITE)) {
            throw NoPermissionException()
        }

        return fileRepository.generatePresignedUploadUrl()
    }

    fun deleteFile(fileId: UUID, authentication: Authentication){

        val user = AuthorizationUtils.onlyUser(authentication)

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(!checkIfUserHasPermissionOnDocument(user, document, DocumentPermission.WRITE)) {
            throw NoPermissionException()
        }

        fileRepository.deleteFile(file.path)
        metadataRepository.deleteById(fileId)
    }

    private fun fetchDocumentData(id: UUID): DocumentDataDto? {
        val url = "$documentServiceHost/api/documents/$id"

        val headers = HttpHeaders().apply {
            add("X-CSRF-Protection", "1")
            add("X-API-KEY", apiKey)
        }

        val entity = HttpEntity<Any>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, DocumentDataDto::class.java)

        return response.body
    }

    private fun getPermissionOfSharedUser(sharedUsers: MutableList<DocumentUserDto>?, id: UUID): DocumentPermission? {
        if (sharedUsers != null) {
            for (user in sharedUsers)
                if (user.userId == id)
                    return user.permission
        }

        return null
    }

    private fun checkIfUserHasPermissionOnDocument(user: CustomUserDetails, document: DocumentDataDto, permission: DocumentPermission): Boolean {
        return (user.id == document.owner || getPermissionOfSharedUser(document.shared, user.id) == permission)
    }
}
