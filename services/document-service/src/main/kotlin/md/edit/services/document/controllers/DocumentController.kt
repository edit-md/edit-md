package md.edit.services.document.controllers

import md.edit.services.document.dtos.DocumentDTO
import md.edit.services.document.dtos.toDocumentDTO
import md.edit.services.document.services.DocumentService
import md.edit.services.document.utils.AuthorizationUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class DocumentController(
    private val documentService: DocumentService
) {
    @GetMapping("/")
    fun getDocuments(authentication: Authentication): ResponseEntity<Collection<DocumentDTO>> {
        // ToDo: Add Pagination
        val user = AuthorizationUtils.onlyUser(authentication)

        val documents = documentService.getDocumentsOfUser(user)
        val documentDTOs = documents.map { it.toDocumentDTO() }.toMutableList()
        return ResponseEntity.ok(documentDTOs)
    }

    @GetMapping("/shared")
    fun getSharedDocuments(authentication: Authentication): ResponseEntity<Collection<DocumentDTO>> {
        val user = AuthorizationUtils.onlyUser(authentication)

        val documents = documentService.getSharedDocumentsOfUser(user)
        val documentDTOs = documents.map { it.toDocumentDTO() }.toMutableList()
        return ResponseEntity.ok(documentDTOs)
    }

    @GetMapping("/owned")
    fun getOwnedDocuments(authentication: Authentication): ResponseEntity<Collection<DocumentDTO>> {
        val user = AuthorizationUtils.onlyUser(authentication)

        val documents = documentService.getOwnedDocumentsOfUser(user)
        val documentDTOs = documents.map { it.toDocumentDTO() }.toMutableList()
        return ResponseEntity.ok(documentDTOs)
    }

    @GetMapping("/{id}")
    fun getDocument(authentication: Authentication, @PathVariable id: String): ResponseEntity<DocumentDTO> {
        val uuid = runCatching { UUID.fromString(id) }.getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        val document = documentService.getDocumentById(uuid) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        // if the request is from an API key, return the document
        if (AuthorizationUtils.isAPI(authentication) != null) {
            return ResponseEntity.ok(document.toDocumentDTO(withShared = true))
        }

        // if the request is from the owner of the document, return the document
        AuthorizationUtils.onlyUsers(authentication, listOf(document.owner.toString()))
        return ResponseEntity.ok(document.toDocumentDTO(withShared = true))
    }

    @DeleteMapping("/{id}")
    fun deleteDocument(authentication: Authentication, @PathVariable id: String): ResponseEntity<Unit> {
        val uuid = runCatching { UUID.fromString(id) }.getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST) }
        val document = documentService.getDocumentById(uuid) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        // if the request is from an API key, delete the document
        if (AuthorizationUtils.isAPI(authentication) != null) {
            documentService.deleteDocument(document)
            return ResponseEntity.ok().build()
        }

        AuthorizationUtils.onlyUser(authentication, document.owner.toString())

        documentService.deleteDocument(document)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/")
    fun createDocument(authentication: Authentication): ResponseEntity<DocumentDTO> {
        // ToDo: Use a DTO for the input data
        val user = AuthorizationUtils.onlyUser(authentication)
        return ResponseEntity.ok(documentService.createDocument(user).toDocumentDTO(withShared = true))
    }
}


