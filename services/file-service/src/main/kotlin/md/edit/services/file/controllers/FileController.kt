package md.edit.services.file.controllers

import md.edit.services.file.services.FileStorageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/files")
class FileController(private val fileStorageService: FileStorageService) {

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val fileName = fileStorageService.storeFile(file)
        return ResponseEntity.ok("File uploaded successfully: $fileName")
    }

    @GetMapping("/{fileId}")
    fun getFile(@PathVariable fileId: Long): ResponseEntity<ByteArray> {
        val fileData = fileStorageService.getFile(fileId)
        return if (fileData != null) {
            ResponseEntity.ok(fileData)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
