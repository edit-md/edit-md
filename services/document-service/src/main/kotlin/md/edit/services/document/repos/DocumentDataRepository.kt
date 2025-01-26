package md.edit.services.document.repos

import jakarta.persistence.LockModeType
import md.edit.services.document.data.DocumentData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import java.util.*

interface DocumentDataRepository : JpaRepository<DocumentData, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun findById(id: UUID): Optional<DocumentData>

}