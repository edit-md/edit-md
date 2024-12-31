package md.edit.services.file.repos

import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.GetObjectArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.errors.MinioException
import io.minio.http.Method
import md.edit.services.file.data.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit

@Repository
class FileRepository(private val minioClient: MinioClient) {

    @Value("\${minio.bucket-name}")
    private lateinit var bucketName: String

    @Throws(IOException::class, MinioException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun uploadFile(file: MultipartFile, filePath: String): String {
        val inputStream = file.inputStream
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(filePath)
                    .stream(inputStream, file.size, -1)
                    .contentType(file.contentType ?: "application/octet-stream")
                    .build()
            )
        } catch (e: MinioException) {
            throw IOException("Error during upload to MinIO: ${e.message}", e)
        } finally {
            inputStream.close()
        }

        return filePath
    }

    @Throws(MinioException::class, IOException::class)
    fun downloadFile(objectName: String): InputStream {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .build()
            )
        } catch (e: MinioException) {
            throw IOException("Error during fetch from MinIO: ${e.message}", e)
        }
    }

    @Throws(MinioException::class, IOException::class)
    fun generatePresidedDownloadUrl(file: File): String {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .`object`(file.path)
                    .method(Method.GET)
                    .expiry(1, TimeUnit.MINUTES)
                    .build()
            )
        } catch (e: MinioException) {
            throw IOException("Error during fetch from MinIO: ${e.message}", e)
        }
    }
}
