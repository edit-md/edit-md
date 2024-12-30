package md.edit.services.document.data

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,

    var title: String,
    var content: String,

    var owner: UUID,

    var visibility: DocumentVisibility = DocumentVisibility.PRIVATE,

    @Temporal(TemporalType.TIMESTAMP)
    var lastModified: Date = Date(),

    @Temporal(TemporalType.TIMESTAMP)
    var created: Date = Date(),

    @OneToMany(
        mappedBy = "document",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var documentUsers: MutableSet<DocumentUser> = mutableSetOf()
) {
    constructor(title: String, content: String, owner: UUID) : this(null, title, content, owner)

    // Helper method for bidirectional relationship
    fun addDocumentUser(documentUser: DocumentUser) {
        documentUsers.add(documentUser)

        if (documentUser.document != this) {  // Prevent infinite recursion
            documentUser.document = this
        }
    }
}