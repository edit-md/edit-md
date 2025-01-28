package md.edit.services.file.exceptions

import io.minio.errors.MinioException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error during communication with MinIO")
class MinIOException: MinioException() {
    override var message: String = "Error during communication with MinIO"
}