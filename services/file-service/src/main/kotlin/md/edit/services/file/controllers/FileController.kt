package md.edit.services.file.controllers

import com.example.fileservice.service.FileStorageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/files")
class FileController(private val fileStorageService: FileStorageService) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val fileName = fileStorageService.storeFile(file)
        return ResponseEntity.ok("File uploaded successfully: $fileName")
    }
}