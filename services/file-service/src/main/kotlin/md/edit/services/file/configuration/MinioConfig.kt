package md.edit.services.file.configuration

import io.minio.MinioClient
import io.minio.errors.MinioException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs

@Configuration
class MinioConfig {

    @Value("\${minio.internal-url}")
    private lateinit var internalEndpoint: String

    @Value("\${minio.public-url}")
    private lateinit var publicEndpoint: String

    @Value("\${minio.access-key}")
    private lateinit var accessKey: String

    @Value("\${minio.secret-key}")
    private lateinit var secretKey: String

    @Value("\${minio.bucket-name}")
    private lateinit var bucketName: String

    @Bean
    fun minioClient(): MinioClient {
        val endpoint = publicEndpoint.ifBlank { internalEndpoint }
        val minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()

        val isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder()
        .bucket(bucketName)
        .build())
        if (!isBucketExist) {
            minioClient.makeBucket(MakeBucketArgs.builder()
            .bucket(bucketName)
            .build())
        }

        return minioClient
    }
}
