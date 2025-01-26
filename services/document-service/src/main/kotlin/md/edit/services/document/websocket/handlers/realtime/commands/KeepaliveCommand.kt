package md.edit.services.document.websocket.handlers.realtime.commands

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class KeepaliveCommand : DocumentCommand() {
    override fun execute(session: WebSocketSession, message: String) {
        // Do nothing
    }
}