package md.edit.services.document.realtime

import io.github.oshai.kotlinlogging.KotlinLogging
import org.postgresql.PGConnection
import org.postgresql.PGNotification
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.sql.SQLException
import java.util.function.Consumer
import javax.sql.DataSource


@Component
class NotificationListener(
    private val dataSource: DataSource
) {

    @Value("\${edit-md.realtime.notifications.timeout:1000}")
    private var timeout: Int = 1000

    @Value("\${edit-md.realtime.notifications.channel:document_changes_channel}")
    private lateinit var notificationChannel: String

    private val log = KotlinLogging.logger {}

    private var shouldStop = false

    @Async
    fun listenForNotifications(consumer: Consumer<PGNotification>) {
        while (!shouldStop) {
            try {
                dataSource.connection.use { c ->
                    val con: PGConnection = c.unwrap(PGConnection::class.java)
                    c.createStatement().execute("LISTEN $notificationChannel")
                    log.info {
                        "Connection established: Listening for notifications on channel: $notificationChannel"
                    }

                    while (true) {
                        val nts = con.getNotifications(timeout) ?: continue
                        for (nt in nts) {
                            consumer.accept(nt!!)
                        }
                    }
                }
            } catch (e: Exception) {
                if(shouldStop) {
                    log.info { "Realtime notification listener stopped" }
                    return
                }

                log.warn { "Error occurred while listening for notifications, attempting to reconnect... $e" }
            }
        }
    }

    @EventListener(ContextClosedEvent::class)
    fun onContextClosedEvent(contextClosedEvent: ContextClosedEvent) {
        shouldStop = true
    }

}