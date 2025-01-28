package md.edit.services.document.repos

import md.edit.services.document.data.DocumentChange
import md.edit.services.document.data.DocumentChangeId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface DocumentChangeRepository : JpaRepository<DocumentChange, DocumentChangeId> {

    @Modifying
    @Query("DELETE DocumentChange dc WHERE dc.id.documentId = :documentId AND dc.id.revision < :revision")
    fun cleanupChanges(documentId: UUID, revision: ULong)

}