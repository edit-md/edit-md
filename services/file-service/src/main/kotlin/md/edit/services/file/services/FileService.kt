package md.edit.services.file.services

import md.edit.services.file.data.DocumentPermission
import md.edit.services.file.data.DocumentVisibility
import md.edit.services.file.repos.FileRepository
import org.springframework.stereotype.Service
import md.edit.services.file.data.File
import md.edit.services.file.dtos.FileDtoOut
import md.edit.services.file.exceptions.DocumentNotFoundException
import md.edit.services.file.exceptions.NoPermissionException
import md.edit.services.file.exceptions.UploadedFileNotFoundException
import md.edit.services.file.repos.FileMetadataRepository
import md.edit.services.file.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
import java.util.*
import kotlin.collections.ArrayList

@Service
class FileService(private val fileRepository: FileRepository,
                  private val metadataRepository: FileMetadataRepository,
                  private val documentService: DocumentService) {

    fun getFileInformation(fileId: UUID, authentication: Authentication): File {
        val user = AuthorizationUtils.onlyUser(authentication)

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(!documentService.checkIfUserHasPermissionOnDocument(user, document, DocumentPermission.READ)) {
            throw NoPermissionException()
        }

        return file
    }

    fun generatePresignedDownloadUrl(fileId: UUID, authentication: Authentication): String {
        val user = AuthorizationUtils.onlyUser(authentication)

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(!documentService.checkIfUserHasPermissionOnDocument(user, document, DocumentPermission.READ) && document.visibility != DocumentVisibility.PUBLIC) {
            throw NoPermissionException()
        }

        return fileRepository.generatePresignedDownloadUrl(file.path)
    }

    fun getAllFilesFromDocument(documentId: UUID, authentication: Authentication): List<File> {
        val user = AuthorizationUtils.onlyUser(authentication)

        val document = documentService.fetchDocumentData(documentId) ?: throw DocumentNotFoundException()

        if(!documentService.checkIfUserHasPermissionOnDocument(user, document, DocumentPermission.READ)) {
            throw NoPermissionException()
        }

        val files = metadataRepository.findByDocumentId(documentId)

        return files
    }

    fun generatePresignedUploadUrl(documentId: UUID, authentication: Authentication): String {
        val user = AuthorizationUtils.onlyUser(authentication)

        val document = documentService.fetchDocumentData(documentId) ?: throw DocumentNotFoundException()

        if(!documentService.checkIfUserHasPermissionOnDocument(user, document, DocumentPermission.WRITE)) {
            throw NoPermissionException()
        }

        return fileRepository.generatePresignedUploadUrl()
    }

    fun deleteFile(fileId: UUID, authentication: Authentication){

        val user = AuthorizationUtils.onlyUser(authentication)

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(!documentService.checkIfUserHasPermissionOnDocument(user, document, DocumentPermission.WRITE)) {
            throw NoPermissionException()
        }

        fileRepository.deleteFile(file.path)
        metadataRepository.deleteById(fileId)
    }
}