package md.edit.services.file.controllers

import md.edit.services.file.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
import md.edit.services.file.services.FileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*

@RestController
@RequestMapping("/api/files")
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
    fun getPresidedDownloadUrl(@PathVariable fileId: UUID, authentication: Authentication): ResponseEntity<String> {
        val user = AuthorizationUtils.onlyUser(authentication)
        // No Permission handling
        val presignedUrl = fileService.generatePresidedDownloadUrl(fileId)
        return ResponseEntity.ok(presignedUrl)
    }
}
