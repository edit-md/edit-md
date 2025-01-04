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
import java.io.FileNotFoundException
import java.io.FileWriter
import java.io.IOException
import java.util.*


@RestController
class FileController(private val fileService: FileService) {

    @GetMapping("/{fileId}")
    fun getFileInformation(@PathVariable fileId: UUID): ResponseEntity<FileDtoOut> {
        try {
            val fileDto = fileService.getFileInformation(fileId)
            return ResponseEntity.ok(fileDto)
        } catch(e: FileNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        }
    }

    @GetMapping("/{fileId}/download")
    fun getPresignedDownloadUrl(@PathVariable fileId: UUID): ResponseEntity<String> {
        try {
            val presignedUrl = fileService.generatePresignedDownloadUrl(fileId)
            return ResponseEntity.ok(presignedUrl)
        } catch(e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @GetMapping
    fun getAllFilesFromDocument(@RequestParam("doc") documentId: UUID): ResponseEntity<List<FileDtoOut>> {
        return ResponseEntity.ok(fileService.getAllFilesFromDocument(documentId))
    }

    @PostMapping
    fun getPresignedUploadUrl(@RequestParam("doc") documentId: UUID): ResponseEntity<String> {
        val presignedUrl = fileService.generatePresignedUploadUrl()
        return ResponseEntity.ok(presignedUrl)
    }

    @DeleteMapping("/{fileId}")
    fun deleteFile(@PathVariable fileId: UUID): ResponseEntity<Boolean> {
        try {
            fileService.deleteFile(fileId)
            return ResponseEntity.ok(true)
        } catch(e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    // Endpoint only for testing purposes
    @PostMapping("/upload")
    fun uploadFile(): ResponseEntity<String> {

        val fileName: String = "example.txt"

        // Make new File and URL
        val presignedUrl : String = fileService.generatePresignedUploadUrl()
        val file: File = File(fileName)
        file.createNewFile()

        // Write something in the file
        val writer: FileWriter = FileWriter(file)
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
            presignedUrl,
            "-d", fileName
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
