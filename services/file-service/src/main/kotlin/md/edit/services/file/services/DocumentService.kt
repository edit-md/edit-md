package md.edit.services.file.services

import md.edit.services.file.data.DocumentPermission
import md.edit.services.file.dtos.DocumentDataDto
import md.edit.services.file.exceptions.DocumentNotFoundException
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

        try {
            val url = "$documentServiceHost/api/documents/$id"

            val headers = HttpHeaders().apply {
                add("X-CSRF-Protection", "1")
                add("X-API-KEY", apiKey)
            }

            val entity = HttpEntity<Any>(headers)
            val response = restTemplate.exchange(url, HttpMethod.GET, entity, DocumentDataDto::class.java)

            return response.body
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getUsersWithPermission(documentId: UUID, permission: DocumentPermission): Array<UUID> {
        val document = fetchDocumentData(documentId) ?: throw DocumentNotFoundException()
        val userList = mutableListOf<UUID>()
        userList.add(document.owner)

        if(permission == DocumentPermission.READ){
            for(user in document.shared ?: emptyList()){
                userList.add(user.userId)
            }
        } else{
            for(user in document.shared ?: emptyList()){
                if(user.permission == DocumentPermission.WRITE){
                    userList.add(user.userId)
                }
            }
        }

        return userList.toTypedArray()
    }
}