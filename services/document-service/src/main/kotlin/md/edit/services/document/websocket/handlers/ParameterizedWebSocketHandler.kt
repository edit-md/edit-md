package md.edit.services.document.websocket.handlers

import md.edit.services.document.configuration.cookieauth.CookieAuthenticationService
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession

abstract class ParameterizedWebSocketHandler(path: String) : AuthenticatedWebSocketHandler() {

    private var cookieAuthService: CookieAuthenticationService? = null

    var path: String = path
        private set

    var fullPath: String = path
        private set

    private var parameters: Map<Int, String> = HashMap()

    fun setInitialParameters(cookieAuthService: CookieAuthenticationService, contextPath: String) {
        super.setInitialParameters(cookieAuthService)
        this.cookieAuthService = cookieAuthService

        fullPath = "${contextPath.trimEnd('/')}/${path.trimStart('/')}"
        initializeParameters()
    }

    init {
        initializeParameters()
    }

    private fun initializeParameters() {
        parameters = HashMap()
        fullPath.split("/").forEachIndexed { index, part ->
            if (part.startsWith("{") && part.endsWith("}")) {
                (parameters as HashMap)[index] = part.substring(1, part.length - 1)
            }
        }
    }

    final override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)

        val requestUri = session.uri?.path?.split("/")

        val parameterValues: MutableList<String> = mutableListOf()

        parameters.forEach { (index, name) ->
            if (requestUri != null && requestUri.size > index) {
                session.attributes[name] = requestUri[index]
                parameterValues.add(requestUri[index])
            }
        }

        afterConnectionEstablished(session, parameterValues)
    }

    abstract fun afterConnectionEstablished(session: WebSocketSession, parameters: Collection<String>)

    abstract override fun handleMessage(session: WebSocketSession, message: WebSocketMessage<*>)

    abstract override fun handleTransportError(session: WebSocketSession, exception: Throwable)

    abstract override fun afterConnectionClosed(session: WebSocketSession, closeStatus: CloseStatus)

    abstract override fun supportsPartialMessages(): Boolean
}