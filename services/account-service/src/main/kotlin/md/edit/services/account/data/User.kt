package md.edit.services.account.data

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,

    var name: String,

    var email: String,

    var avatar: String? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    var connectedAccounts: MutableList<ConnectedAccount> = mutableListOf() // Using List for immutability
) {
    constructor(name: String, email: String, avatar: String?) : this(null, name, email, avatar)
}