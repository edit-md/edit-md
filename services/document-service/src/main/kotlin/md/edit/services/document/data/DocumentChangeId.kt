package md.edit.services.document.data

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class DocumentChangeId(
    @Column(name = "document_id")
    var documentId: UUID?,

    @Column(name = "revision")
    var revision: ULong
) : Serializable {
    constructor(document: Document, revision: ULong) : this(document.id, revision)
}