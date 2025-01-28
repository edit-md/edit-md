package md.edit.services.document.websocket.handlers

import md.edit.services.document.configuration.cookieauth.CookieAuthenticationService
import md.edit.services.document.configuration.cookieauth.CustomUserDetails
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.socket.*

abstract class AuthenticatedWebSocketHandler : WebSocketHandler {

    private var cookieAuthService: CookieAuthenticationService? = null

    fun setInitialParameters(cookieAuthService: CookieAuthenticationService) {
        this.cookieAuthService = cookieAuthService
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val cookieName = cookieAuthService?.sessionCookieName ?: return
        val cookies = session.handshakeHeaders.getFirst("Cookie")

        // Find the cookie value
        val cookie = cookies?.split(";")
            ?.map { cookiePart -> cookiePart.trim() }
            ?.firstOrNull { cookiePart -> cookiePart.startsWith("$cookieName=") }
            ?.substringAfter("$cookieName=")

        if(cookie == null) {
            return
        }

        val userData = cookieAuthService!!.fetchUserData(cookie)

        if(userData == null) {
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(
            CustomUserDetails(userData), null, null
        )

        session.attributes["auth"] = authentication
    }

    abstract override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>)

    abstract override fun handleTransportError(session: WebSocketSession, exception: Throwable)

    abstract override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus)

    abstract override fun supportsPartialMessages(): Boolean

    fun WebSocketSession.getAuthentication(): Authentication? {
        return this.attributes.getOrElse("auth") { null } as Authentication?
    }
}
