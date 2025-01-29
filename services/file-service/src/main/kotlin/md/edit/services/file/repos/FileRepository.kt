package md.edit.services.file.repos

import io.minio.*
import io.minio.errors.MinioException
import io.minio.http.Method
import md.edit.services.file.exceptions.MinIOException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.io.IOException
import java.io.InputStream
import java.util.UUID
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
            throw MinIOException()
        }
    }

    fun generatePresignedUploadUrl(id: UUID): String {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .`object`(id.toString())
                    .method(Method.PUT)
                    .expiry(1, TimeUnit.MINUTES)
                    .build()
            )
        } catch (e: MinioException) {
            throw MinIOException()
        }
    }

    fun getInputStreamOfImage(filePath: String): InputStream {
        try{
            val stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(filePath)
                    .build()
            )

            return stream
        } catch(e: MinioException) {
            throw MinIOException()
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
            throw MinIOException()
        }
    }
}
