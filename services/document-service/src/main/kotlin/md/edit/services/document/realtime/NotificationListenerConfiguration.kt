package md.edit.services.document.realtime

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NotificationListenerConfiguration(
    private val listener: NotificationListener
) {

    @Bean
    fun startListener(handler: NotificationHandler): CommandLineRunner {
        return CommandLineRunner {
            listener.listenForNotifications(handler)
        }
    }

}