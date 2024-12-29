package md.edit.services.document.controllers

import md.edit.services.document.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DocumentController {
    @GetMapping("/")
    fun getDocument(authentication: Authentication): String {
        val api = AuthorizationUtils.onlyAPI(authentication)

        return "Document for API key ${api.principal}"
    }

    @GetMapping("/me")
    fun getMe(authentication: Authentication): String {
        val user = AuthorizationUtils.onlyUser(authentication)
        return "Document for user ${user.username} with id ${user.id} and email ${user.email}"
    }
}