package md.edit.services.document.repos

import md.edit.services.document.data.Document
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface DocumentRepository : JpaRepository<Document, UUID> {
    @Query("SELECT d FROM Document d WHERE d.owner = :owner ORDER BY d.data.lastModified DESC, d.created DESC")
    fun findByOwner(owner: UUID): MutableList<Document>

    @Query("SELECT d FROM Document d JOIN DocumentUser du ON d.id = du.id.documentId WHERE du.id.userId = :userId ORDER BY d.data.lastModified DESC, d.created DESC")
    fun findByShared(userId: UUID): MutableList<Document>

    @Query("SELECT d FROM Document d LEFT JOIN DocumentUser du ON d.id = du.id.documentId WHERE d.owner = :userId OR du.id.userId = :userId ORDER BY d.data.lastModified DESC, d.created DESC")
    fun findByUser(userId: UUID): MutableList<Document>
}