package md.edit.services.document.websocket.handlers.realtime.commands

import org.springframework.security.core.Authentication
import org.springframework.web.socket.WebSocketSession

abstract class DocumentCommand {
    abstract fun execute(session: WebSocketSession, message: String)

    fun WebSocketSession.getAuthentication(): Authentication? {
        return this.attributes.getOrElse("auth") { null } as Authentication?
    }
}