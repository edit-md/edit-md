package md.edit.services.document.services

import md.edit.services.document.configuration.cookieauth.CustomUserDetails
import md.edit.services.document.data.Document
import md.edit.services.document.data.DocumentPermission
import md.edit.services.document.data.DocumentUser
import md.edit.services.document.data.DocumentUserId
import md.edit.services.document.repos.DocumentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DocumentService(
    private val documentRepository: DocumentRepository
) {

    @Transactional
    fun getDocumentById(id: UUID): Document? {
        val document = documentRepository.findById(id).orElse(null) ?: return null
        return document
    }

    @Transactional
    fun createDocument(user: CustomUserDetails): Document {
        // ToDo: Use correct data
        var document = Document("test", "content", user.id)
        document = documentRepository.save(document);

        val user1Id = UUID.randomUUID()
        val user2Id = UUID.randomUUID()
        val user1 = DocumentUser(DocumentUserId(document.id!!, user1Id), permission = DocumentPermission.READ)
        val user2 = DocumentUser(DocumentUserId(document.id!!, user2Id), permission = DocumentPermission.WRITE)

        document.addDocumentUser(user1)
        document.addDocumentUser(user2)

        return documentRepository.save(document);
    }

    @Transactional
    fun getDocumentsOfUser(user: CustomUserDetails): Collection<Document> {
        val owned = documentRepository.findByOwner(user.id)
        val shared = documentRepository.findByShared(user.id)

        owned.addAll(shared)

        return owned
    }

    @Transactional
    fun getOwnedDocumentsOfUser(user: CustomUserDetails): Collection<Document> {
        return documentRepository.findByOwner(user.id)
    }

    @Transactional
    fun getSharedDocumentsOfUser(user: CustomUserDetails): Collection<Document> {
        return documentRepository.findByShared(user.id)
    }

}