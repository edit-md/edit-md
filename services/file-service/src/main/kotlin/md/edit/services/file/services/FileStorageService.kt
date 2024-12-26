package md.edit.services.file.services

import io.minio.MinioClient
import io.minio.errors.MinioException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

@Service
class FileStorageService(private val minioClient: MinioClient, private val bucketName: String) {

    fun storeFile(file: MultipartFile): String {
        val fileName = file.originalFilename ?: throw IllegalArgumentException("Invalid file name")
        val inputStream: InputStream = file.inputStream

        try {
            minioClient.putObject(bucketName, fileName, inputStream, file.size, null, null, file.contentType)
        } catch (e: MinioException) {
            throw RuntimeException("Error occurred while uploading the file: $e")
        }
        return fileName
    }
}