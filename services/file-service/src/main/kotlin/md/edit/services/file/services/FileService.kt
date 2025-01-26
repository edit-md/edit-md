package md.edit.services.file.services

import md.edit.services.file.data.DocumentPermission
import md.edit.services.file.data.DocumentVisibility
import md.edit.services.file.repos.FileRepository
import org.springframework.stereotype.Service
import md.edit.services.file.data.File
import md.edit.services.file.exceptions.DocumentNotFoundException
import md.edit.services.file.exceptions.UploadedFileNotFoundException
import md.edit.services.file.repos.FileMetadataRepository
import md.edit.services.file.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
import java.util.*

@Service
class FileService(private val fileRepository: FileRepository,
                  private val metadataRepository: FileMetadataRepository,
                  private val documentService: DocumentService) {

    fun getFileInformation(fileId: UUID, authentication: Authentication): File {

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.READ))

        return file
    }

    fun generatePresignedDownloadUrl(fileId: UUID, authentication: Authentication): String {

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(document.visibility == DocumentVisibility.PRIVATE)
            AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.READ))
        else
            AuthorizationUtils.onlyUser(authentication)

        return fileRepository.generatePresignedDownloadUrl(file.path)
    }

    fun getAllFilesFromDocument(documentId: UUID, authentication: Authentication): List<File> {

        val document = documentService.fetchDocumentData(documentId) ?: throw DocumentNotFoundException()

        AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.READ))

        val files = metadataRepository.findByDocumentId(documentId)

        return files
    }

    fun generatePresignedUploadUrl(documentId: UUID, authentication: Authentication): String {

        val document = documentService.fetchDocumentData(documentId) ?: throw DocumentNotFoundException()

        AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.WRITE))

        return fileRepository.generatePresignedUploadUrl()
    }

    fun deleteFile(fileId: UUID, authentication: Authentication){

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.WRITE))

        fileRepository.deleteFile(file.path)
        metadataRepository.deleteById(fileId)
    }
}