package md.edit.services.document.data

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "document_data")
data class DocumentData(
    @Id
    var id: UUID?, // Same ID as the Document entity

    var content: String,

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    var document: Document,

    @Temporal(TemporalType.TIMESTAMP)
    var lastModified: Date = Date.from(Instant.EPOCH)
) {
    constructor(document: Document, content: String) : this(null, content, document)
}