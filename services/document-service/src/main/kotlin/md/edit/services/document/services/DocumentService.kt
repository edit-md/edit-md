package md.edit.services.document.services

import md.edit.services.document.data.*
import md.edit.services.document.exceptions.DocumentNotFoundException
import md.edit.services.document.exceptions.UserNotFoundException
import md.edit.services.document.repos.DocumentChangeRepository
import md.edit.services.document.repos.DocumentRepository
import md.edit.services.document.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val documentChangeRepository: DocumentChangeRepository
) {

    @Transactional
    fun hasReadAccess(authentication: Authentication?, id: UUID): Boolean {
        val document = documentRepository.findById(id).orElseThrow { DocumentNotFoundException() }

        // Allow public documents to be read by anyone
        if (document.visibility == DocumentVisibility.PUBLIC) return true

        // Deny access if not authenticated
        if (authentication == null) return false

        // Allow API access without further checks
        if (AuthorizationUtils.isAPI(authentication)) return true

        // Check access for private documents
        AuthorizationUtils.runCatching {
            onlyUsers(
                authentication,
                document.owner,
                *document.documentUsers.map { it.id.userId }.toTypedArray()
            )
        }.getOrElse { return false }

        return true
    }

    @Transactional
    fun hasWriteAccess(authentication: Authentication?, id: UUID): Boolean {
        // Deny access if not authenticated
        if (authentication == null) return false

        val document = documentRepository.findById(id).orElseThrow { DocumentNotFoundException() }

        // Allow API access without further checks
        if (AuthorizationUtils.isAPI(authentication)) return true

        // Check access for private documents
        AuthorizationUtils.runCatching {
            onlyUsers(
                authentication,
                document.owner,
                *document.documentUsers.filter { it.permission == DocumentPermission.WRITE }.map { it.id.userId }.toTypedArray()
            )
        }.getOrElse { return false }

        return true
    }

    @Transactional
    fun getDocument(authentication: Authentication?, id: UUID): Document {
        val document = documentRepository.findById(id).orElseThrow { DocumentNotFoundException() }

        // Allow API access without further checks
        if (AuthorizationUtils.isAPI(authentication)) return document

        // Check access for private documents
        if (document.visibility == DocumentVisibility.PRIVATE) {
            AuthorizationUtils.onlyUsers(
                authentication,
                document.owner,
                *document.documentUsers.map { it.id.userId }.toTypedArray()
            )
        }

        return document
    }

    @Transactional
    fun createDocument(
        authentication: Authentication?,
        title: String,
        visibility: DocumentVisibility?,
        shares: Map<UUID, DocumentPermission>?
    ): Document {
        val owner = AuthorizationUtils.onlyUser(authentication)

        var document = Document(title, owner.id, visibility ?: DocumentVisibility.PRIVATE)
        val documentData = DocumentData(document, "")
        document.data = documentData

        document = documentRepository.save(document)

        // Save the initial state
        val documentChange = DocumentChange(
            DocumentChangeId(document.id, 0.toULong()),
            owner.id,
            null,
            0.toULong(),
            DocumentChangeType.INSERT,
            "",
            0.toULong()
        )
        documentChangeRepository.save(documentChange)

        if (shares == null) {
            return document
        }

        shares.forEach { (userId, permission) ->
            val user = DocumentUser(DocumentUserId(document, userId), document, permission)
            document.documentUsers.add(user)
        }

        return documentRepository.save(document)
    }

    @Transactional
    fun getDocuments(authentication: Authentication?): Collection<Document> {
        if (AuthorizationUtils.isAPI(authentication)) {
            return documentRepository.findAll()
        }

        val user = AuthorizationUtils.onlyUser(authentication)
        return documentRepository.findByUser(user.id)
    }

    @Transactional
    fun getOwnedDocuments(authentication: Authentication?): Collection<Document> {
        val user = AuthorizationUtils.onlyUser(authentication)
        return documentRepository.findByOwner(user.id)
    }

    @Transactional
    fun getSharedDocuments(authentication: Authentication?): Collection<Document> {
        val user = AuthorizationUtils.onlyUser(authentication)
        return documentRepository.findByShared(user.id)
    }

    @Transactional
    fun updateDocument(
        authentication: Authentication?,
        documentId: UUID,
        title: String?,
        visibility: DocumentVisibility?
    ): Document {
        val document = documentRepository.findById(documentId).orElseThrow { DocumentNotFoundException() }

        AuthorizationUtils.onlyUsers(authentication, document.owner)

        document.title = title ?: document.title
        document.visibility = visibility ?: document.visibility

        return documentRepository.save(document)
    }

    @Transactional
    fun deleteDocument(authentication: Authentication?, documentId: UUID) {
        val document = documentRepository.findById(documentId).orElseThrow { DocumentNotFoundException() }

        // Check access (API clients bypass, others must be the owner)
        if (!AuthorizationUtils.isAPI(authentication)) {
            AuthorizationUtils.onlyUser(authentication, document.owner)
        }

        documentRepository.delete(document)
    }

    @Transactional
    fun getSharedUsers(authentication: Authentication?, documentId: UUID): Collection<DocumentUser> {
        val document = documentRepository.findById(documentId).orElseThrow { DocumentNotFoundException() }

        // Check access (API clients bypass, others must be the allowed users)
        if (!AuthorizationUtils.isAPI(authentication)) {
            val allowedUsers = arrayOf(document.owner) + document.documentUsers.map { it.id.userId }
            AuthorizationUtils.onlyUsers(authentication, *allowedUsers)
        }

        return document.documentUsers
    }

    @Transactional
    fun addSharedUser(
        authentication: Authentication?,
        documentId: UUID,
        userId: UUID,
        permission: DocumentPermission
    ): DocumentUser {
        var document = documentRepository.findById(documentId).orElseThrow { DocumentNotFoundException() }

        // Check access (API clients bypass, others must be the owner)
        if (!AuthorizationUtils.isAPI(authentication)) {
            AuthorizationUtils.onlyUser(authentication, document.owner)
        }

        val documentUser = DocumentUser(DocumentUserId(document, userId), document, permission)
        document.documentUsers.add(documentUser)

        document = documentRepository.save(document)
        return document.documentUsers.firstOrNull { it.id.userId == userId } ?: throw UserNotFoundException()
    }

    @Transactional
    fun removeSharedUser(authentication: Authentication?, documentId: UUID, userId: UUID) {
        val document = documentRepository.findById(documentId).orElseThrow { DocumentNotFoundException() }

        // Check access (API clients bypass, others must be the owner)
        if (!AuthorizationUtils.isAPI(authentication)) {
            AuthorizationUtils.onlyUser(authentication, document.owner)
        }

        val removed = document.documentUsers.removeIf { it.id.userId == userId }

        if (!removed) {
            throw UserNotFoundException()
        }

        documentRepository.save(document)
    }

    @Transactional
    fun searchTitles(authentication: Authentication?, searchTerm: String): Collection<Document> {
        val user = AuthorizationUtils.onlyUser(authentication)
        return documentRepository.findDocumentsByTitle(searchTerm, user.id)
    }
}