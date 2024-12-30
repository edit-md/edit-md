package md.edit.services.document.data

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
data class DocumentUserId(
    @Column(name = "document_id")
    var documentId: UUID,

    @Column(name = "user_id")
    var userId: UUID
) : Serializable