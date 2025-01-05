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
        val fileDto = fileService.getFileInformation(fileId, authentication)
        return ResponseEntity.ok(fileDto)
    }

    @GetMapping("/{fileId}/download")
    fun getPresignedDownloadUrl(@PathVariable fileId: UUID, authentication: Authentication): ResponseEntity<String> {
        val presignedUrl = fileService.generatePresignedDownloadUrl(fileId, authentication)
        return ResponseEntity.ok(presignedUrl)
    }

    @GetMapping
    fun getAllFilesFromDocument(@RequestParam("doc") documentId: UUID, authentication: Authentication): ResponseEntity<List<FileDtoOut>> {
        return ResponseEntity.ok(fileService.getAllFilesFromDocument(documentId, authentication))
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

    // Endpoint only for testing purposes
    @PostMapping("/upload")
    fun uploadFile(@RequestParam("doc") documentId: UUID, authentication: Authentication): ResponseEntity<String> {

        val fileName = "example.txt"

        // Make new File and URL
        val presignedUrl : String = fileService.generatePresignedUploadUrl(documentId, authentication)
        val file = File(fileName)
        file.createNewFile()

        // Write something in the file
        val writer = FileWriter(file)
        try {
            writer.write("I was born on the cemetery\n")
            writer.write("Under the sign of the moon")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally{
            writer.close()
        }

        // Upload the file
        val command = arrayOf(
            "curl",
            "-X", "PUT",
            "--upload-file", fileName,
            presignedUrl
        )

        try {

            val processBuilder = ProcessBuilder(*command)
            val process = processBuilder.start()


            val exitCode = process.waitFor()
            return ResponseEntity.ok("Prozess beendet mit Code: $exitCode")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return ResponseEntity(HttpStatus.CONFLICT)
    }
}
