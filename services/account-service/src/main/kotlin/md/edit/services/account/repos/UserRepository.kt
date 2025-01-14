package md.edit.services.account.repos

import md.edit.services.account.data.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?

    @Query(
        value = """
            SELECT *
            FROM users
            ORDER BY similarity(name, :searchTerm) DESC
            LIMIT 10;
        """,
        nativeQuery = true
    )
    fun findUsersByName(searchTerm: String): Collection<User>
}