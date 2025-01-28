package md.edit.services.document.data

import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import java.util.*

@Entity
@Table(name = "document_changes")
data class DocumentChange(

    @EmbeddedId
    var id: DocumentChangeId,

    var userId: UUID,
    var websocketId: String?,

    var index: ULong,

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    var changeType: DocumentChangeType,

    var content: String?,
    var length: ULong,

    @Temporal(TemporalType.TIMESTAMP)
    var created: Date = Date(),

    ) {
    constructor(documentChange: DocumentChange, index: ULong? = null, length: ULong? = null) : this(
        documentChange.id,
        documentChange.userId,
        documentChange.websocketId,
        documentChange.index,
        documentChange.changeType,
        documentChange.content,
        documentChange.length,
        documentChange.created,
        ) {
        if (index != null) {
            this.index = index
        }

        if (length != null) {
            this.length = length
        }
    }
}

enum class DocumentChangeType {
    INSERT,
    DELETE
}