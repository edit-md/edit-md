package md.edit.services.document.data

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "document")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,

    var title: String,

    var owner: UUID,

    @Enumerated(EnumType.STRING)
    var visibility: DocumentVisibility = DocumentVisibility.PRIVATE,

    @Temporal(TemporalType.TIMESTAMP)
    var created: Date = Date(),

    @OneToMany(
        mappedBy = "document",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var documentUsers: MutableSet<DocumentUser> = mutableSetOf(),

    @OneToOne(mappedBy = "document", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var data: DocumentData? = null
) {
    constructor(title: String, owner: UUID, visibility: DocumentVisibility) : this(null, title, owner, visibility)
}

enum class DocumentVisibility {
    PUBLIC,
    PRIVATE
}