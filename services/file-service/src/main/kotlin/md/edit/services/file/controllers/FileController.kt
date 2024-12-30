package md.edit.services.file.controller

import md.edit.services.file.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
import md.edit.services.file.service.FileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

@RestController
@RequestMapping("/api/files")
class FileController(private val fileService: FileService) {

    @PostMapping("/upload")
    @Throws(IOException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun uploadFile(@RequestParam("file") file: MultipartFile, authentication: Authentication): ResponseEntity<String> {
        //val user = AuthorizationUtils.onlyUser(authentication) // Need to look over the configuration classes
        val fileName = fileService.uploadFile(file)
        return ResponseEntity.ok("File uploaded: $fileName")
    }

    @GetMapping("/download/{fileName}")
    @Throws(IOException::class)
    fun downloadFile(@PathVariable fileName: String, authentication: Authentication): ResponseEntity<ByteArray> {
        //val user = AuthorizationUtils.onlyUser(authentication) // Need to look over the configuration classes
        val inputStream = fileService.downloadFile(fileName)
        val content = inputStream.readAllBytes()
        return ResponseEntity.ok(content)
    }
}
