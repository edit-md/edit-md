package md.edit.services.file.configuration

import io.minio.MinioClient
import io.minio.errors.MinioException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig {

    @Value("\${minio.endpoint}")
    lateinit var endpoint: String

    @Value("\${minio.access-key}")
    lateinit var accessKey: String

    @Value("\${minio.secret-key}")
    lateinit var secretKey: String

    @Value("\${minio.bucket-name}")
    lateinit var bucketName: String

    @Bean
    fun minioClient(): MinioClient {
        val minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()

        val isBucketExist = minioClient.bucketExists(bucketName)
        if (!isBucketExist) {
            minioClient.makeBucket(bucketName)
        }

        return minioClient
    }
}
