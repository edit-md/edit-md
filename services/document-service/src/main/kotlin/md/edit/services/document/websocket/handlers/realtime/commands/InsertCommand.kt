package md.edit.services.document.websocket.handlers.realtime.commands

import com.fasterxml.jackson.databind.ObjectMapper
import md.edit.services.document.data.DocumentChangeType
import md.edit.services.document.dtos.DocumentChangeInsertDTO
import md.edit.services.document.exceptions.InvalidIdException
import md.edit.services.document.services.DocumentRealtimeService
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.*

@Component
class InsertCommand(
    private val documentRealtimeService: DocumentRealtimeService,
    private val objectMapper: ObjectMapper
) : DocumentCommand() {
    override fun execute(session: WebSocketSession, message: String) {
        val documentId = runCatching { UUID.fromString(session.attributes["docId"] as String) }.getOrElse { throw InvalidIdException() }
        val change = objectMapper.runCatching { readValue(message, DocumentChangeInsertDTO::class.java) }.getOrElse { throw RuntimeException("Failed to parse message", it) }

        val savedChange = documentRealtimeService.documentInsert(
            session.getAuthentication(),
            session.id,
            documentId,
            change.revision,
            DocumentChangeType.INSERT,
            change.index,
            change.content
        )
    }
}