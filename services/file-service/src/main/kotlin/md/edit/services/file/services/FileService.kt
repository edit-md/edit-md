package md.edit.services.file.service

import md.edit.services.file.repos.FileRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import io.minio.errors.MinioException

@Service
class FileService(private val fileRepository: FileRepository) {

    @Throws(IOException::class, MinioException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun uploadFile(file: MultipartFile): String {
        return fileRepository.uploadFile(file)
    }

    @Throws(MinioException::class, IOException::class)
    fun downloadFile(objectName: String): InputStream {
        return fileRepository.downloadFile(objectName)
    }
}
