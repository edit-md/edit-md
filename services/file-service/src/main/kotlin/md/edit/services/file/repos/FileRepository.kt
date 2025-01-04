package md.edit.services.file.repos

import io.minio.*
import io.minio.errors.MinioException
import io.minio.http.Method
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.TimeUnit

@Repository
class FileRepository(private val minioClient: MinioClient) {

    @Value("\${minio.bucket-name}")
    private lateinit var bucketName: String

    fun generatePresignedDownloadUrl(filePath: String): String {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .`object`(filePath)
                    .method(Method.GET)
                    .expiry(1, TimeUnit.MINUTES)
                    .build()
            )
        } catch (e: MinioException) {
            throw IOException("Error during fetch from MinIO: ${e.message}", e)
        }
    }

    fun generatePresignedUploadUrl(): String {
        val placeholderObjectName = "temporary-upload-${System.currentTimeMillis()}"
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .`object`(placeholderObjectName)
                    .method(Method.PUT)
                    .expiry(1, TimeUnit.MINUTES)
                    .build()
            )
        } catch (e: MinioException) {
            throw IOException("Error from MinIO: ${e.message}", e)
        }
    }

    fun deleteFile(filePath: String) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(filePath)
                    .build()
            )
        } catch (e: MinioException) {
            throw IOException("Error during delete from MinIO: ${e.message}", e)
        }
    }
}
