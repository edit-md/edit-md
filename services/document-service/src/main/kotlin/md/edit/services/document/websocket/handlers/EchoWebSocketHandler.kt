package md.edit.services.document.websocket.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import md.edit.services.document.dtos.toDTO
import md.edit.services.document.exceptions.InvalidIdException
import md.edit.services.document.services.DocumentService
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import java.util.*

@Component
class EchoWebSocketHandler(
    private val documentService: DocumentService,
    private val objectMapper: ObjectMapper
) : ParameterizedWebSocketHandler("/{docId}/live") {

    private val logger = KotlinLogging.logger {}

    override fun afterConnectionEstablished(session: WebSocketSession, parameters: Collection<String>) {
        try {
            val documentId = runCatching { UUID.fromString(session.attributes["docId"] as String) }.getOrElse { throw InvalidIdException() }
            val authentication = session.attributes["userData"] as Authentication

            val document = documentService.getDocument(authentication, documentId)

            writeMessage(session, document.toDTO(
                withShared = true,
                withContent = true,
            ))

            logger.info { "Connection established from ${session.remoteAddress} ${session.id}" }
        } catch (e: Exception) {
            // get annotation from exception
            e::class.annotations.find {
                it.annotationClass == ResponseStatus::class
            }?.let {
                val status = it as ResponseStatus
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason(status.reason))
            }
        }
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {
        logger.info { "Received message: $message" }

        if (message is TextMessage) {
            val text = message.payload
            session.sendMessage(TextMessage("Echo: $text ${session.attributes["docId"]}"))
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        session.close(CloseStatus.SERVER_ERROR)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        logger.info { "Connection closed from ${session.remoteAddress}" }
    }

    override fun supportsPartialMessages(): Boolean {
        return true
    }

    fun writeMessage(session: WebSocketSession, data: Any) {
        session.sendMessage(TextMessage(objectMapper.writeValueAsString(data)))
    }
}