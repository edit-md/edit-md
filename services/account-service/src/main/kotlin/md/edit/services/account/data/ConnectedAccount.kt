package md.edit.services.account.data

import jakarta.persistence.*

@Entity
@Table(name = "connected_accounts", uniqueConstraints = [UniqueConstraint(columnNames = ["provider", "user_id"])])
data class ConnectedAccount(
    @EmbeddedId
    var id: ConnectedAccountId, // Composite key consisting of provider and remoteId

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,
) {
    constructor(provider: String, remoteId: String, user: User) : this(ConnectedAccountId(provider, remoteId), user)

    override fun toString(): String {
        return "ConnectedAccount(id=$id, user=${user.id})"
    }
}