package md.edit.services.file.controllers

import md.edit.services.file.dtos.FileDtoOut
import md.edit.services.file.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
import md.edit.services.file.services.FileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*

@RestController
@RequestMapping
class FileController(private val fileService: FileService) {

    @PostMapping("/upload")
    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun uploadFile(@RequestParam("file") file: MultipartFile, authentication: Authentication): ResponseEntity<String> {
        val user = AuthorizationUtils.onlyUser(authentication)
        val fileName = fileService.uploadFile(file)
        return ResponseEntity.ok("File uploaded: $fileName")
    }

    @GetMapping("/download/{fileName}")
    @Throws(IOException::class)
    fun downloadFile(@PathVariable fileName: String, authentication: Authentication): ResponseEntity<ByteArray> {
        val user = AuthorizationUtils.onlyUser(authentication)
        val inputStream = fileService.downloadFile(fileName)
        val content = inputStream.readAllBytes()
        return ResponseEntity.ok(content)
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
