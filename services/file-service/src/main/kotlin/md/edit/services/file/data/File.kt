package md.edit.services.file.data

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "files")
data class File(
    @Id
    val id: UUID,

    @Column(name = "document_id", nullable = false)
    val documentId: UUID,

    @Column(name = "path", nullable = false)
    val path: String,

    @Column(name = "type", nullable = false)
    val type: String,

    @Column(name = "created_date", nullable = false)
    val createdDate: java.time.LocalDateTime,

    @Column(name = "uploaded", nullable = false)
    val uploaded: Boolean,

    @Column(name = "file_size", nullable = false)
    val fileSize: Long
) {
    constructor(
        documentId: UUID,
        path: String,
        type: String,
        createdDate: java.time.LocalDateTime,
        fileSize: Long
    ) : this(
        id = UUID.randomUUID(),
        documentId = documentId,
        path = path,
        type = type,
        createdDate = createdDate,
        uploaded = false,
        fileSize = fileSize
    )
}
