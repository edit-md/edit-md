package md.edit.services.account.repos

import md.edit.services.account.data.ConnectedAccount
import md.edit.services.account.data.ConnectedAccountId
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ConnectedAccountRepository: JpaRepository<ConnectedAccount, ConnectedAccountId> {
    override fun findById(id: ConnectedAccountId): Optional<ConnectedAccount>
}