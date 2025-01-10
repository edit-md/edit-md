package md.edit.services.file.services

import md.edit.services.file.configuration.cookieauth.CustomUserDetails
import md.edit.services.file.data.DocumentPermission
import md.edit.services.file.dtos.DocumentDataDto
import md.edit.services.file.dtos.DocumentUserDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class DocumentService(private val restTemplate: RestTemplate) {

    @Value("\${edit-md.document-service.host}")
    private lateinit var documentServiceHost: String

    @Value("\${edit-md.api-key}")
    private lateinit var apiKey: String

    fun fetchDocumentData(id: UUID): DocumentDataDto? {
        val url = "$documentServiceHost/api/documents/$id"

        val headers = HttpHeaders().apply {
            add("X-CSRF-Protection", "1")
            add("X-API-KEY", apiKey)
        }

        val entity = HttpEntity<Any>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, DocumentDataDto::class.java)

        return response.body
    }

    fun getPermissionOfSharedUser(sharedUsers: MutableList<DocumentUserDto>?, id: UUID): DocumentPermission? {
        if (sharedUsers != null) {
            for (user in sharedUsers)
                if (user.userId == id)
                    return user.permission
        }

        return null
    }

    fun checkIfUserHasPermissionOnDocument(user: CustomUserDetails, document: DocumentDataDto, permission: DocumentPermission): Boolean {
        return (user.id == document.owner || getPermissionOfSharedUser(document.shared, user.id) == permission)
    }
}