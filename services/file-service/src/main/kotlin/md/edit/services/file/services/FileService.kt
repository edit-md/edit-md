package md.edit.services.file.services

import md.edit.services.file.data.DocumentPermission
import md.edit.services.file.data.DocumentVisibility
import md.edit.services.file.repos.FileRepository
import org.springframework.stereotype.Service
import md.edit.services.file.data.File
import md.edit.services.file.exceptions.DocumentNotFoundException
import md.edit.services.file.exceptions.FileAlreadyUploadedException
import md.edit.services.file.exceptions.NotAnImageException
import md.edit.services.file.exceptions.UploadedFileNotFoundException
import md.edit.services.file.repos.FileMetadataRepository
import md.edit.services.file.utils.AuthorizationUtils
import org.springframework.core.io.InputStreamResource
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.*

@Service
class FileService(private val fileRepository: FileRepository,
                  private val metadataRepository: FileMetadataRepository,
                  private val documentService: DocumentService) {

    fun getFileInformation(fileId: UUID, authentication: Authentication?): File {

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(document.visibility == DocumentVisibility.PRIVATE)
            AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.READ))

        return file
    }

    fun generatePresignedDownloadUrl(fileId: UUID, authentication: Authentication?): String {

        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(document.visibility == DocumentVisibility.PRIVATE)
            AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.READ))

        return fileRepository.generatePresignedDownloadUrl(file.path)
    }

    fun getInputStreamOfImage(fileId: UUID, authentication: Authentication?): InputStreamResource {
        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        if (!file.type.startsWith("image/"))
            throw NotAnImageException()

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        if(document.visibility == DocumentVisibility.PRIVATE)
            AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.READ))

        val stream = fileRepository.getInputStreamOfImage(file.path.substring(file.path.lastIndexOf('/') + 1))

        return InputStreamResource(stream)

    }

    fun getAllFilesFromDocument(documentId: UUID, authentication: Authentication?): List<File> {

        val document = documentService.fetchDocumentData(documentId) ?: throw DocumentNotFoundException()

        if(document.visibility == DocumentVisibility.PRIVATE)
            AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.READ))

        val files = metadataRepository.findByDocumentId(documentId)

        val uploadedFiles = files.filter { it.uploaded }

        return uploadedFiles
    }

    fun saveUploadRequest(documentId: UUID, type: String, authentication: Authentication): File {

        val document = documentService.fetchDocumentData(documentId) ?: throw DocumentNotFoundException()

        AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.WRITE))

        val file = File(documentId, type, LocalDateTime.now())

        metadataRepository.save(file)

        return file
    }

    fun updateUploadedStateOfFile(fileId: UUID, uploaded: Boolean, authentication: Authentication): Boolean {
        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        if(file.uploaded)
            throw FileAlreadyUploadedException()

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.WRITE))

        file.uploaded = uploaded

        metadataRepository.save(file)

        return uploaded
    }

    fun updateFilesizeStateOfFile(fileId: UUID, authentication: Authentication): Long{
        val file = metadataRepository.findById(fileId)
            .orElseThrow { UploadedFileNotFoundException() }

        val document = documentService.fetchDocumentData(file.documentId) ?: throw DocumentNotFoundException()

        AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.WRITE))

        val filesize = fileRepository.getFilesize(fileId)

        file.fileSize = filesize

        metadataRepository.save(file)

        return filesize
    }

    fun generatePresignedUploadUrl(fileId: UUID, documentId: UUID, authentication: Authentication): String {

        val document = documentService.fetchDocumentData(documentId) ?: throw DocumentNotFoundException()

        AuthorizationUtils.onlyUsers(authentication, *documentService.getUsersWithPermission(document.id, DocumentPermission.WRITE))

        return fileRepository.generatePresignedUploadUrl(fileId)
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