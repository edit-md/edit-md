package md.edit.services.document.websocket.handlers.realtime.commands

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import md.edit.services.document.data.DocumentChangeType
import md.edit.services.document.dtos.DocumentChangeInsertDTO
import md.edit.services.document.exceptions.InvalidIdException
import md.edit.services.document.services.DocumentRealtimeService
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.*

@Component
class InsertCommand(
    private val documentRealtimeService: DocumentRealtimeService,
    private val objectMapper: ObjectMapper,
    private val redissonClient: RedissonClient
) : DocumentCommand() {
    private val logger = KotlinLogging.logger { }

    override fun execute(session: WebSocketSession, message: String) {
        val documentId = runCatching { UUID.fromString(session.attributes["docId"] as String) }.getOrElse { throw InvalidIdException() }
        val change = objectMapper.runCatching { readValue(message, DocumentChangeInsertDTO::class.java) }.getOrElse { throw RuntimeException("Failed to parse message", it) }

        val lock = redissonClient.getLock("document:$documentId")
        lock.lock()
        logger.trace { "LOCKED \"document:$documentId\"" }

        try {
            val savedChange = documentRealtimeService.documentInsert(
                session.getAuthentication(),
                session.id,
                documentId,
                change.revision,
                DocumentChangeType.INSERT,
                change.index,
                change.content
            )

            logger.trace { "InsertCommand: $savedChange" }
        } finally {
            lock.unlock()
            logger.trace { "UNLOCKED \"document:$documentId\"" }
        }

    }
}