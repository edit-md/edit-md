package md.edit.services.file.controllers

import md.edit.services.file.dtos.*
import md.edit.services.file.services.FileService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import java.util.concurrent.TimeUnit


@RestController
class FileController(private val fileService: FileService) {

    @GetMapping("/{fileId}")
    fun getFileInformation(@PathVariable fileId: UUID, authentication: Authentication?): ResponseEntity<FileDtoOut> {
        val file = fileService.getFileInformation(fileId, authentication)
        return ResponseEntity.ok(FileDtoOut(file))
    }

    @GetMapping("/{fileId}/download")
    fun getPresignedDownloadUrl(@PathVariable fileId: UUID, authentication: Authentication?): ResponseEntity<PresignedDownloadURLDtoOut> {
        val file = fileService.getFileInformation(fileId, authentication)
        val presignedUrl = fileService.generatePresignedDownloadUrl(fileId, authentication)
        return ResponseEntity.ok(PresignedDownloadURLDtoOut(file, presignedUrl))
    }

    @GetMapping("image/{fileId}/download")
    fun downloadImage(@PathVariable fileId: UUID, authentication: Authentication?): ResponseEntity<InputStreamResource> {
        val file = fileService.getFileInformation(fileId, authentication)
        val fileName = file.name
        val fileType = file.type.substring(file.type.lastIndexOf('/') + 1)
        val resource = fileService.getInputStreamOfImage(fileId, authentication)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${fileName}.${fileType}\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES))
            .body(resource)
    }

    @GetMapping("/")
    fun getAllFilesFromDocument(@RequestParam("doc") documentId: UUID, authentication: Authentication?): ResponseEntity<List<FileDtoOut>> {
        val files = fileService.getAllFilesFromDocument(documentId, authentication)
        return ResponseEntity.ok(files.map{FileDtoOut(it)})
    }

    @PostMapping("/")
    fun getPresignedUploadUrl(@RequestBody presignedUploadURLDtoIn: PresignedUploadURLDtoIn, authentication: Authentication): ResponseEntity<PresignedUploadURLDtoOut> {
        val file = fileService.saveUploadRequest(presignedUploadURLDtoIn.document, presignedUploadURLDtoIn.name, presignedUploadURLDtoIn.type, authentication)
        val presignedUrl = fileService.generatePresignedUploadUrl(file.id, presignedUploadURLDtoIn.document, authentication)
        return ResponseEntity.ok(PresignedUploadURLDtoOut(file.id, presignedUrl))
    }

    @PatchMapping("/{fileId}")
    fun updateFileMetadata(@PathVariable fileId: UUID, @RequestBody updateFileDtoIn: UpdateFileDtoIn, authentication: Authentication): ResponseEntity<FileDtoOut> {
        val file = fileService.updateUploadedStateOfFile(fileId, updateFileDtoIn.uploaded, authentication)
        fileService.updateFilesizeStateOfFile(fileId, authentication)

        return ResponseEntity.ok(FileDtoOut(file))
    }

    @DeleteMapping("/{fileId}")
    fun deleteFile(@PathVariable fileId: UUID, authentication: Authentication): ResponseEntity<FileDtoOut> {
        try {
            val file = fileService.deleteFile(fileId, authentication)
            return ResponseEntity.ok(FileDtoOut(file))
        } catch(e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}
