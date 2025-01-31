package md.edit.services.document.websocket.handlers.realtime.commands

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import md.edit.services.document.data.DocumentChangeType
import md.edit.services.document.dtos.DocumentChangeDeleteDTO
import md.edit.services.document.exceptions.InvalidIdException
import md.edit.services.document.services.DocumentRealtimeService
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.*

@Component
class DeleteCommand(
    private val documentRealtimeService: DocumentRealtimeService,
    private val objectMapper: ObjectMapper,
    private val redissonClient: RedissonClient
) : DocumentCommand() {

    private val logger = KotlinLogging.logger { }

    override fun execute(session: WebSocketSession, message: String) {
        val documentId =
            runCatching { UUID.fromString(session.attributes["docId"] as String) }.getOrElse { throw InvalidIdException() }
        val change = objectMapper.runCatching { readValue(message, DocumentChangeDeleteDTO::class.java) }
            .getOrElse { throw RuntimeException("Failed to parse message") }

        val lock = redissonClient.getLock("document:$documentId")
        lock.lock()
        logger.trace { "LOCKED \"document:$documentId\"" }

        try {
            val savedChange = documentRealtimeService.documentDelete(
                session.getAuthentication(),
                session.id,
                documentId,
                change.revision,
                DocumentChangeType.DELETE,
                change.index,
                change.length
            )

            logger.trace { "DeleteCommand: $savedChange" }
        } finally {
            lock.unlock()
            logger.trace { "UNLOCKED \"document:$documentId\"" }
        }
    }
}