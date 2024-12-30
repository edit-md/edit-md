package md.edit.services.file.repos

import io.minio.MinioClient
import io.minio.errors.MinioException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

@Repository
class FileRepository(private val minioClient: MinioClient) {

    @Value("\${minio.bucket-name}")
    private lateinit var bucketName: String

    @Throws(IOException::class, MinioException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun uploadFile(file: MultipartFile): String {
        val objectName = file.originalFilename ?: throw IOException("filename is null")

        file.inputStream.use { inputStream ->
            try {
                minioClient.putObject(bucketName, objectName, inputStream, file.contentType ?: "application/octet-stream")
            } catch (e: MinioException) {
                throw MinioException("Error during upload to MinIO: ${e.message}")
            }
        }

        return objectName
    }

    @Throws(MinioException::class, IOException::class)
    fun downloadFile(objectName: String): InputStream {
        try {
            return minioClient.getObject(bucketName, objectName)
        } catch (e: MinioException) {
            throw MinioException("Error during fetch from MinIO: ${e.message}")
        }
    }
}
