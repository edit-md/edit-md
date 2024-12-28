package md.edit.services.account.data

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class ConnectedAccountId(
    var provider: String,
    var remoteId: String
) : Serializable