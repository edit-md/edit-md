package md.edit.services.document.websocket

import io.github.oshai.kotlinlogging.KotlinLogging
import md.edit.services.document.configuration.cookieauth.CookieAuthenticationService
import md.edit.services.document.websocket.handlers.ParameterizedWebSocketHandler
import md.edit.services.document.websocket.handlers.realtime.DocumentRealtimeHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val cookieAuthenticationService: CookieAuthenticationService,
    private val documentRealtimeHandler: DocumentRealtimeHandler
) : WebSocketConfigurer {

    @Value("\${server.servlet.context-path:}")
    private lateinit var contextPath: String

    private var logger = KotlinLogging.logger {}

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        logger.warn { "Registering WebSocket handlers" }

        registerWebSocketHandler(registry, documentRealtimeHandler)
    }

    fun registerWebSocketHandler(registry: WebSocketHandlerRegistry, handler: ParameterizedWebSocketHandler) {
        handler.setInitialParameters(cookieAuthenticationService, contextPath)

        logger.info { "Registering WebSocket handler for path ${handler.path} (${handler.fullPath})" }
        registry.addHandler(handler, handler.path).setAllowedOrigins("*")
    }
}