package md.edit.services.document.services

import md.edit.services.document.data.*
import md.edit.services.document.exceptions.AuthorizationException
import md.edit.services.document.exceptions.DocumentNotFoundException
import md.edit.services.document.repos.DocumentChangeRepository
import md.edit.services.document.repos.DocumentDataRepository
import md.edit.services.document.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class DocumentRealtimeService(
    private val documentService: DocumentService,
    private val documentDataRepository: DocumentDataRepository,
    private val documentChangeRepository: DocumentChangeRepository
) {

    @Transactional
    fun documentInsert(
        authentication: Authentication?,
        websocketId: String,
        documentId: UUID,
        revision: ULong,
        changeType: DocumentChangeType,
        index: ULong,
        content: String
    ): DocumentChange {
        if(!documentService.hasWriteAccess(authentication, documentId) || authentication == null) {
            throw AuthorizationException()
        }

        val user = AuthorizationUtils.onlyUser(authentication)

        val documentContent = documentDataRepository.findById(documentId).orElseThrow { DocumentNotFoundException() }
        documentContent.revision++

        var documentChange = DocumentChange(
            id = DocumentChangeId(documentId, revision),
            userId = user.id,
            websocketId = websocketId,
            changeType = changeType,
            index = index,
            content = content,
            length = content.length.toULong(),
        )

        documentChange =
            applyChange(documentContent, documentChange) ?: throw RuntimeException("Failed to apply change")

        documentChangeRepository.save(documentChange)
        documentContent.lastModified = Date()
        documentDataRepository.save(documentContent)

        if ((documentContent.revision % 20u).toUInt() == 0u) {
            documentChangeRepository.cleanupChanges(documentId, documentContent.revision - 10u)
        }

        return documentChange
    }

    @Transactional
    fun documentDelete(
        authentication: Authentication?,
        websocketId: String,
        documentId: UUID,
        revision: ULong,
        changeType: DocumentChangeType,
        index: ULong,
        length: ULong
    ): DocumentChange {
        if(!documentService.hasWriteAccess(authentication, documentId) || authentication == null) {
            throw AuthorizationException()
        }

        val user = AuthorizationUtils.onlyUser(authentication)

        val documentContent =
            documentDataRepository.findById(documentId).orElseThrow { DocumentNotFoundException() }
        documentContent.revision++

        var documentChange = DocumentChange(
            id = DocumentChangeId(documentId, revision),
            userId = user.id,
            websocketId = websocketId,
            changeType = changeType,
            index = index,
            content = null,
            length = length
        )

        documentChange =
            applyChange(documentContent, documentChange) ?: throw RuntimeException("Failed to apply change")

        documentChangeRepository.save(documentChange)
        documentContent.lastModified = Date()
        documentDataRepository.save(documentContent)

        if ((documentContent.revision % 20u).toUInt() == 0u) {
            documentChangeRepository.cleanupChanges(documentId, documentContent.revision - 10u)
        }

        return documentChange
    }

    private fun applyChange(document: DocumentData, c: DocumentChange): DocumentChange {
        var change = c
        val changeRevision = change.id.revision
        val documentRevision = document.revision

        if (changeRevision > documentRevision) {
            throw RuntimeException("Change revision is too far ahead")
        }


        for (i in changeRevision + 1u until documentRevision) {
            val doneOperation = documentChangeRepository.findById(DocumentChangeId(document.id, i))
                .orElseThrow { RuntimeException("Failed to find change") }
            val transformedOperation =
                transformChange(change, doneOperation) ?: throw RuntimeException("Failed to transform change")
            change = transformedOperation
        }

        change.id.revision = documentRevision

        applyOperation(document, change)
        return change
    }

    private fun transformChange(op1: DocumentChange, op2: DocumentChange): DocumentChange? {
        if (op1.changeType == DocumentChangeType.INSERT && op2.changeType == DocumentChangeType.INSERT) {
            if (op1.index < op2.index) {
                return op1
            }

            return DocumentChange(op1, index = op1.index + op2.length)
        }

        if (op1.changeType == DocumentChangeType.DELETE && op2.changeType == DocumentChangeType.DELETE) {
            val delete1End = op1.index + op1.length
            val delete2End = op2.index + op2.length

            if (delete1End <= op2.index) {
                return op1 // delete1 occurs entirely before delete2
            }

            if (delete2End <= op1.index) {
                return DocumentChange(op1, index = op1.index - op2.length)
            }

            // Overlapping delete operations
            val start = minOf(op1.index, op2.index)
            val end = maxOf(delete1End, delete2End)

            return DocumentChange(op1, index = start, length = (end - start))
        }

        if (op1.changeType == DocumentChangeType.INSERT && op2.changeType == DocumentChangeType.DELETE) {
            val deleteEnd = op2.index + op2.length

            if (op1.index < op2.index) {
                return op1
            }

            if (op1.index in op2.index until deleteEnd) {
                // Insert occurs within the delete range, discard the insert
                return null
            }

            return DocumentChange(op1, index = op1.index - op2.length)
        }

        if (op1.changeType == DocumentChangeType.DELETE && op2.changeType == DocumentChangeType.INSERT) {
            if (op1.index >= op2.index) {
                return DocumentChange(op1, index = op1.index + op2.length)
            }

            return op1
        }

        return null
    }

    private fun applyOperation(document: DocumentData, operation: DocumentChange) {
        try {
            document.content = when (operation.changeType) {
                DocumentChangeType.INSERT -> {
                    val position = operation.index.coerceIn(0u, document.content.length.toULong()).toInt()
                    document.content.substring(0, position) + operation.content + document.content.substring(position)
                }

                DocumentChangeType.DELETE -> {
                    val position = operation.index.toInt().coerceIn(0, document.content.length)
                    val length = operation.length.toInt().coerceIn(0, document.content.length - position)
                    document.content.removeRange(position, position + length)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}