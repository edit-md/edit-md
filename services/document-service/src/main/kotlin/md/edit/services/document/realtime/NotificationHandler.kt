package md.edit.services.document.realtime

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import md.edit.services.document.data.DocumentChange
import md.edit.services.document.websocket.handlers.realtime.DocumentRealtimeHandler
import org.postgresql.PGNotification
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer

@Component
class NotificationHandler(
    private val documentRealtimeHandler: DocumentRealtimeHandler,
) : Consumer<PGNotification> {


    private val objectMapper = ObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        this.registerKotlinModule()
    }

    @Transactional
    override fun accept(t: PGNotification) {
        // convert t.parameter to DocumentChange
        val objectChange = objectMapper.readValue(t.parameter, DocumentChange::class.java)

        println("Received notification: $objectChange")

        documentRealtimeHandler.broadcastChange(objectChange.id.documentId!!, objectChange)
    }

}