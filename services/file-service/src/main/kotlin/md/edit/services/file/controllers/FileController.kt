package md.edit.services.file.controllers

import md.edit.services.file.dtos.*
import md.edit.services.file.services.FileService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*


@RestController
class FileController(private val fileService: FileService) {

    @GetMapping("/{fileId}")
    fun getFileInformation(@PathVariable fileId: UUID, authentication: Authentication?): ResponseEntity<FileDtoOut> {
        val file = fileService.getFileInformation(fileId, authentication)
        return ResponseEntity.ok(FileDtoOut(file))
    }

    @GetMapping("/{fileId}/download")
    fun getPresignedDownloadUrl(@PathVariable fileId: UUID, authentication: Authentication?): ResponseEntity<String> {
        val presignedUrl = fileService.generatePresignedDownloadUrl(fileId, authentication)
        return ResponseEntity.ok(presignedUrl)
    }

    @GetMapping("image/{fileId}/download")
    fun downloadImage(@PathVariable fileId: UUID, authentication: Authentication?): ResponseEntity<InputStreamResource> {
        val file = fileService.getFileInformation(fileId, authentication)
        val fileName = file.path.substring(file.path.lastIndexOf('/') + 1)
        val resource = fileService.getInputStreamOfImage(fileId, authentication)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${fileName}\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource)
    }

    @GetMapping("/")
    fun getAllFilesFromDocument(@RequestParam("doc") documentId: UUID, authentication: Authentication?): ResponseEntity<List<FileDtoOut>> {
        val files = fileService.getAllFilesFromDocument(documentId, authentication)
        return ResponseEntity.ok(files.map{FileDtoOut(it)})
    }

    @PostMapping("/")
    fun getPresignedUploadUrl(@RequestBody presignedUploadURLDtoIn: PresignedUploadURLDtoIn, authentication: Authentication): ResponseEntity<PresignedUploadURLDtoOut> {
        val file = fileService.saveUploadRequest(presignedUploadURLDtoIn.document, presignedUploadURLDtoIn.type, authentication)
        val presignedUrl = fileService.generatePresignedUploadUrl(file.id, presignedUploadURLDtoIn.document, authentication)
        return ResponseEntity.ok(PresignedUploadURLDtoOut(file.id, presignedUrl))
    }

    @PatchMapping("/{fileId}")
    fun updateFileMetadata(@PathVariable fileId: UUID, @RequestBody updateFileDtoIn: UpdateFileDtoIn, authentication: Authentication): ResponseEntity<UpdateFileDtoOut> {
        fileService.updateUploadedStateOfFile(fileId, updateFileDtoIn.uploaded, authentication)
        val size = fileService.updateFilesizeStateOfFile(fileId, authentication)

        return ResponseEntity.ok(UpdateFileDtoOut(size))
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
