package md.edit.services.file.services

import md.edit.services.file.repos.FileRepository
import md.edit.services.file.data.File
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class FileStorageService(private val fileRepository: FileRepository) {

    fun storeFile(file: MultipartFile): String {
        val fileName = file.originalFilename ?: throw IllegalArgumentException("Invalid file name")

        // Konvertieren der Datei in Byte-Array
        val fileData = file.bytes

        // Erstellen eines File-Objekts und Speichern in der Datenbank
        val fileEntity = File(filename = fileName, fileData = fileData)
        fileRepository.save(fileEntity)

        return fileName
    }

    fun getFile(fileId: Long): ByteArray? {
        // Abrufen der Datei aus der Datenbank
        val fileEntity = fileRepository.findById(fileId).orElse(null)
        return fileEntity?.fileData
    }
}
