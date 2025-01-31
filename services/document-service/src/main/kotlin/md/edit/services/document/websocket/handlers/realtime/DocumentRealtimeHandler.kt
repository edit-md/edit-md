package md.edit.services.document.websocket.handlers.realtime

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging
import md.edit.services.document.data.DocumentChange
import md.edit.services.document.dtos.toDTO
import md.edit.services.document.exceptions.InvalidIdException
import md.edit.services.document.services.DocumentService
import md.edit.services.document.websocket.handlers.ParameterizedWebSocketHandler
import md.edit.services.document.websocket.handlers.realtime.commands.DeleteCommand
import md.edit.services.document.websocket.handlers.realtime.commands.DocumentCommand
import md.edit.services.document.websocket.handlers.realtime.commands.InsertCommand
import md.edit.services.document.websocket.handlers.realtime.commands.KeepaliveCommand
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Controller
class DocumentRealtimeHandler(
    private val documentService: DocumentService,
    private val insertCommand: InsertCommand,
    private val deleteCommand: DeleteCommand,
    private val keepaliveCommand: KeepaliveCommand
) : ParameterizedWebSocketHandler("/{docId}/live") {

    private val logger = KotlinLogging.logger { }

    private val commands = mutableMapOf<UInt, DocumentCommand>()
    private var sessions: ConcurrentMap<UUID, ConcurrentMap<String, WebSocketSession>> = ConcurrentHashMap()
    private val sessionsLock = Any()

    private val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
        enable(SerializationFeature.WRITE_ENUMS_USING_INDEX)
    }

    init {
        initializeCommands()
    }

    private fun initializeCommands() {
        commands[0u] = insertCommand
        commands[1u] = deleteCommand
        commands[255u] = keepaliveCommand
    }

    override fun afterConnectionEstablished(session: WebSocketSession, parameters: Collection<String>) {
        val documentId = runCatching { UUID.fromString(session.attributes["docId"] as String) }.getOrElse { throw InvalidIdException(it) }
        if(!documentService.hasReadAccess(session.getAuthentication(), documentId)) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("No read access"))
            return
        }

        synchronized(sessionsLock) {
            if(sessions[documentId] == null) {
                logger.debug { "Creating new session map for document  $documentId" }
                sessions[documentId] = ConcurrentHashMap()
            }

            logger.debug { "Adding session ${session.id} to document $documentId" }
            sessions[documentId]?.put(session.id, session)
        }
    }

    override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>) {

        if (message is BinaryMessage) {
            try {
                val payloadBuffer = message.payload

                // Extract the first byte as the command ID
                val commandId = (payloadBuffer.get().toInt() and 0xFF).toUInt()

                // Slice the buffer to skip the first byte
                val remainingPayloadBuffer = payloadBuffer.slice()

                // Convert the remaining payload to a UTF-8 string
                val messageString = Charsets.UTF_8.decode(remainingPayloadBuffer).toString()

                // Handle the decompressed data
                val command = commands[commandId]
                command?.execute(session, messageString) ?: throw RuntimeException("Unknown command ID: $commandId")
            } catch (e: Exception) {
                e::class.annotations.find {
                    it.annotationClass == ResponseStatus::class
                }?.let {
                    val status = it as ResponseStatus
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason(status.reason))
                    return
                }

                e.printStackTrace()
            }

        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE)
        }

    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        session.close(CloseStatus.SERVER_ERROR)
    }

    override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus) {
        val documentId = runCatching { UUID.fromString(session.attributes["docId"] as String) }.getOrElse { throw InvalidIdException() }

        synchronized(sessionsLock) {
            sessions[documentId]?.remove(session.id)
            logger.debug { "Removed session ${session.id} from document $documentId" }

            if(sessions[documentId]?.isEmpty() == true) {
                sessions.remove(documentId)
                logger.debug { "Removed session map for document $documentId" }
            }
        }
    }

    override fun supportsPartialMessages(): Boolean {
        return false
    }

    fun broadcastChange(documentId: UUID, documentChange: DocumentChange) {
        if(sessions[documentId] == null) {
            return
        }

        val sessionsToRemove = mutableListOf<String>()

        val textPayload = objectMapper.writeValueAsString(documentChange.toDTO())
        val binaryPayload = textPayload.toByteArray(Charsets.UTF_8)

        val receiverPayload = BinaryMessage(byteArrayOf(0x00) + binaryPayload)
        val creatorPayload = BinaryMessage(byteArrayOf(0x01) + binaryPayload)

        sessions[documentId]?.forEach { (_, session) ->
            try {
                if(documentChange.websocketId == session.id) {
                    session.sendMessage(creatorPayload) // Send message with acknowledgment flag
                } else {
                    session.sendMessage(receiverPayload)
                }
            } catch (e: Exception) {
                sessionsToRemove.add(session.id)
                logger.debug { "Failed to send change to session ${session.id}" }
            }
        }

        sessionsToRemove.forEach { sessionId ->
            sessions[documentId]?.remove(sessionId)
        }
    }

}