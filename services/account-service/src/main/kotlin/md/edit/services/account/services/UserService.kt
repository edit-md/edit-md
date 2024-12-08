package md.edit.services.account.services

import md.edit.services.account.data.ConnectedAccount
import md.edit.services.account.data.ConnectedAccountId
import md.edit.services.account.data.User
import md.edit.services.account.repos.ConnectedAccountRepository
import md.edit.services.account.repos.UserRepository
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val connectedAccountRepository: ConnectedAccountRepository,
    private val userRepository: UserRepository
) {

    /**
     * Gets a user and updates it if it exists, otherwise creates a new user
     * @param oAuth2User OAuth2 user
     * @return User
     */
    @Transactional
    fun getAndUpdateOrCreateUser(oAuth2User: OAuth2User): User {
        val attributes = oAuth2User.attributes

        var user = getUser(oAuth2User)

        if (user == null) {
            // create user
            val accountOrigin = attributes["accountOrigin"].toString()
            val remoteId = attributes["id"].toString()
            val name = attributes["name"].toString()
            val email = attributes["email"].toString()
            val avatar = attributes["avatar"].toString().let { it.ifEmpty { null } }

            var newUser = User(name, email, avatar)
            newUser = userRepository.save(newUser)
            newUser.connectedAccounts.add(ConnectedAccount(accountOrigin, remoteId, newUser))

            user = userRepository.save(newUser)
        } else {
            // update user
            val name = attributes["name"].toString()
            val email = attributes["email"].toString()
            val avatar = attributes["avatar"].toString().let { it.ifEmpty { null } }

            user.name = name
            user.email = email
            user.avatar = avatar

            user = userRepository.save(user)
        }

        return user
    }

    /**
     * Gets a user by OAuth2 user
     * @param oAuth2User OAuth2 user
     * @return User
     */
    @Transactional
    fun getUser(oAuth2User: OAuth2User): User? {
        // get user from database
        val accountOrigin = oAuth2User.attributes["accountOrigin"].toString()
        val remoteId = oAuth2User.attributes["id"].toString()

        // get user from database
        return connectedAccountRepository.findById(ConnectedAccountId(accountOrigin, remoteId)).map { it.user }.orElse(null)
    }

    /**
     * Gets a user by id
     * @param id User id
     * @return User
     */
    @Transactional
    fun getUserById(id: UUID): User? {
        return userRepository.findById(id).map { it }.orElse(null)
    }

}