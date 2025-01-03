package md.edit.services.file.controllers

import md.edit.services.file.dtos.FileDtoOut
import md.edit.services.file.services.FileService
import md.edit.services.file.utils.AuthorizationUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*


@RestController
class FileController(private val fileService: FileService) {

    // Endpoint only for testing purposes
    @PostMapping("/upload")
    fun uploadFile(): ResponseEntity<String> {

        // Make new File and URL
        val presignedUrl : String = fileService.generatePresignedUploadUrl()
        val file: File = File("example.txt")
        file.createNewFile()
        val url: URL = URI(presignedUrl).toURL()

        // Write something in the file
        try {
            val writer: FileWriter = FileWriter(file)

            writer.write("I was born on the cemetery\n")
            writer.write("Under the sign of the moon")

            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Upload the file
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setDoOutput(true)
        connection.setRequestMethod("PUT")
        connection.setRequestProperty("Content-Type", "application/octet-stream")

        connection.getOutputStream().use { outputStream ->
            FileInputStream(file).use { fileInputStream ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while ((fileInputStream.read(buffer).also { bytesRead = it }) != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }

        val responseCode: Int = connection.getResponseCode()
        return ResponseEntity.ok("Response Code: $responseCode")
    }

    @GetMapping("/{fileId}/download")
    fun getPresignedDownloadUrl(@PathVariable fileId: UUID, authentication: Authentication): ResponseEntity<String> {
        try {
            val user = AuthorizationUtils.onlyUser(authentication)
            // No Permission handling
            val presignedUrl = fileService.generatePresignedDownloadUrl(fileId)
            return ResponseEntity.ok(presignedUrl)
        } catch(e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PostMapping
    fun getPresignedUploadUrl(@RequestParam("doc") documentId: UUID, authentication: Authentication): ResponseEntity<String> {
        val user = AuthorizationUtils.onlyUser(authentication)
        // No permission handling
        val presignedUrl = fileService.generatePresignedUploadUrl()
        return ResponseEntity.ok(presignedUrl)
    }

    @GetMapping("/{fileId}")
    fun getFileInformation(@PathVariable fileId: UUID): ResponseEntity<FileDtoOut> {
        try {
            val fileDto = fileService.getFileInformation(fileId)
            return ResponseEntity.ok(fileDto)
        } catch(e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @DeleteMapping("/{fileId}")
    fun deleteFile(@PathVariable fileId: UUID, authentication: Authentication): ResponseEntity<Boolean> {
        try {
            val user = AuthorizationUtils.onlyUser(authentication)
            // No permission handling
            fileService.deleteFile(fileId)
            return ResponseEntity.ok(true)
        } catch(e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}
