package md.edit.services.document.data

import jakarta.persistence.*

@Entity
@Table(name = "document_users")
data class DocumentUser(
    @EmbeddedId
    var id: DocumentUserId,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    var document: Document? = null,

    var permission: DocumentPermission
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DocumentUser) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}