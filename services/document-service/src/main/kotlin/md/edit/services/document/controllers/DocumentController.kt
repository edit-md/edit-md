package md.edit.services.document.controllers

import jakarta.validation.Valid
import md.edit.services.document.dtos.*
import md.edit.services.document.services.DocumentService
import md.edit.services.document.utils.AuthorizationUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors

@RestController
class DocumentController(
    private val documentService: DocumentService
) {
    @GetMapping("/")
    fun getDocuments(authentication: Authentication?): ResponseEntity<Collection<DocumentDTO>> {
        // ToDo: Add Pagination
        val documents = documentService.getDocuments(authentication)
        return ResponseEntity.ok(documents.map { it.toDTO() }.toList())
    }

    @PostMapping("/")
    fun createDocument(
        authentication: Authentication?,
        @Valid @RequestBody data: DocumentCreateDTO
    ): ResponseEntity<DocumentDTO> {
        val document = documentService.createDocument(
            authentication,
            data.title,
            data.visibility,
            data.shared?.associate { it.userId to it.permission })

        return ResponseEntity.ok(document.toDTO(withShared = true))
    }

    @GetMapping("/shared")
    fun getSharedDocuments(authentication: Authentication?): ResponseEntity<Collection<DocumentDTO>> {
        val documents = documentService.getSharedDocuments(authentication)
        return ResponseEntity.ok(documents.map { it.toDTO() }.toList())
    }

    @GetMapping("/owned")
    fun getOwnedDocuments(authentication: Authentication?): ResponseEntity<Collection<DocumentDTO>> {
        val documents = documentService.getOwnedDocuments(authentication)
        return ResponseEntity.ok(documents.map { it.toDTO() }.toList())
    }

    @GetMapping("/{id}")
    fun getDocument(authentication: Authentication?, @PathVariable id: UUID): ResponseEntity<DocumentDTO> {
        val document = documentService.getDocument(authentication, id)
        return ResponseEntity.ok(
            document.toDTO(
                withContent = true,
                withShared = AuthorizationUtils.isUser(
                    authentication,
                    document.owner,
                    *document.documentUsers.map { it.id.userId }.toTypedArray()
                ) || AuthorizationUtils.isAPI(authentication)
            )
        )
    }

    @PatchMapping("/{id}")
    fun updateDocument(
        authentication: Authentication?,
        @PathVariable id: UUID,
        @Valid @RequestBody data: DocumentPatchDTO
    ): ResponseEntity<DocumentDTO> {
        val document = documentService.updateDocument(authentication, id, data.title, data.visibility)
        return ResponseEntity.ok(document.toDTO(withShared = true))
    }

    @DeleteMapping("/{id}")
    fun deleteDocument(authentication: Authentication?, @PathVariable id: UUID): ResponseEntity<Unit> {
        documentService.deleteDocument(authentication, id)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}/share")
    fun getSharedUsers(
        authentication: Authentication?,
        @PathVariable id: UUID
    ): ResponseEntity<Collection<DocumentUserDTO>> {
        val sharedUsers = documentService.getSharedUsers(authentication, id)
        return ResponseEntity.ok(sharedUsers.map { it.toDTO() }.toList())
    }

    @PutMapping("/{id}/share")
    fun shareDocument(
        authentication: Authentication?,
        @PathVariable id: UUID,
        @RequestBody data: DocumentUserInDTO
    ): ResponseEntity<DocumentUserDTO> {
        val addedUser = documentService.addSharedUser(authentication, id, data.userId, data.permission)
        return ResponseEntity.ok(addedUser.toDTO())
    }

    @DeleteMapping("/{id}/share/{userId}")
    fun unshareDocument(
        authentication: Authentication?,
        @PathVariable id: UUID,
        @PathVariable userId: UUID
    ): ResponseEntity<Unit> {
        documentService.removeSharedUser(authentication, id, userId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/search")
    fun getDocumentsByTitle(
        authentication: Authentication?,
        @RequestParam title: String
    ): ResponseEntity<Collection<DocumentDTO>> {
        return ResponseEntity.ok(
            documentService.searchTitles(authentication, title).map
        { it.toDTO() }.toList()
        )
    }
}


