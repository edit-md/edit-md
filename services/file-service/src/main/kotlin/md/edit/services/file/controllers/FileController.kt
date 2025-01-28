package md.edit.services.file.controllers

import md.edit.services.file.dtos.FileDtoOut
import md.edit.services.file.services.FileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*


@RestController
class FileController(private val fileService: FileService) {

    @GetMapping("/{fileId}")
    fun getFileInformation(@PathVariable fileId: UUID, authentication: Authentication): ResponseEntity<FileDtoOut> {
        val file = fileService.getFileInformation(fileId, authentication)
        return ResponseEntity.ok(FileDtoOut(file))
    }

    @GetMapping("/{fileId}/download")
    fun getPresignedDownloadUrl(@PathVariable fileId: UUID, authentication: Authentication): ResponseEntity<String> {
        val presignedUrl = fileService.generatePresignedDownloadUrl(fileId, authentication)
        return ResponseEntity.ok(presignedUrl)
    }

    @GetMapping("/")
    fun getAllFilesFromDocument(@RequestParam("doc") documentId: UUID, authentication: Authentication): ResponseEntity<List<FileDtoOut>> {
        val files = fileService.getAllFilesFromDocument(documentId, authentication)
        return ResponseEntity.ok(files.map{FileDtoOut(it)})
    }

    @PostMapping("/")
    fun getPresignedUploadUrl(@RequestParam("doc") documentId: UUID, authentication: Authentication): ResponseEntity<String> {
        val presignedUrl = fileService.generatePresignedUploadUrl(documentId, authentication)
        return ResponseEntity.ok(presignedUrl)
    }

    @DeleteMapping("/{fileId}")
    fun deleteFile(@PathVariable fileId: UUID, authentication: Authentication): ResponseEntity<Boolean> {
        try {
            fileService.deleteFile(fileId, authentication)
            return ResponseEntity.ok(true)
        } catch(e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}
