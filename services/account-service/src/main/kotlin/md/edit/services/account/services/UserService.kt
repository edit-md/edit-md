package md.edit.services.account.services

import md.edit.services.account.configuration.oauth.CustomOAuth2User
import md.edit.services.account.configuration.oauth.CustomOAuth2UserRequest
import md.edit.services.account.data.ConnectedAccount
import md.edit.services.account.data.ConnectedAccountId
import md.edit.services.account.data.User
import md.edit.services.account.data.UserSettings
import md.edit.services.account.repos.ConnectedAccountRepository
import md.edit.services.account.repos.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

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
    fun createOrUpdateAndGetUser(request: CustomOAuth2UserRequest): User {
        var user = getUser(request)

        if (user == null) {
            // create user
            val accountOrigin = request.accountOrigin
            val remoteId = request.remoteId
            val name = request.name
            val email = request.email
            val avatar = request.avatar

            var newUser = User(name, email, avatar)
            newUser = userRepository.save(newUser)
            newUser.connectedAccounts.add(ConnectedAccount(accountOrigin, remoteId, newUser))

            user = userRepository.save(newUser)
        } else {
            // update user
            user.name = request.name
            user.email = request.email
            user.avatar = request.avatar

            user = userRepository.save(user)
        }

        return user
    }

    /**
     * Gets a user by OAuth2UserRequest
     * @param request OAuth2UserRequest
     * @return User
     */
    @Transactional
    fun getUser(request: CustomOAuth2UserRequest): User? {
        // get user from database
        val accountOrigin = request.accountOrigin
        val remoteId = request.remoteId

        // get user from database
        return connectedAccountRepository.findById(ConnectedAccountId(accountOrigin, remoteId)).map { it.user }
            .orElse(null)
    }

    @Transactional
    fun getUser(oAuth2User: CustomOAuth2User): User? {
        return userRepository.findById(oAuth2User.id).map { it }.orElse(null)
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

    @Transactional
    fun searchNames(searchTerm: String): Collection<User> {
        val projections = userRepository.findUsersByName(searchTerm)
        return projections.map {
            User(
                id = it.id,
                name = it.name,
                email = it.email,
                avatar = it.avatar,
                settings = UserSettings.DEFAULT, // Default-Werte setzen
                connectedAccounts = mutableListOf() // Leere Liste, da nicht geladen
            )
        }
    }

    @Transactional
    fun updateUserSettings(user: User, newUserSettings: UserSettings): User {

        // Iterate through all properties of the newUserSettings object
        for (property in newUserSettings::class.declaredMemberProperties) {
            // Get the value of the current property from newUserSettings
            val value = property.call(newUserSettings)

            // If the value is not null, update the corresponding property in user.settings
            if (value != null) {
                // Find the matching property in user.settings based on the property name
                val userSettingsProp = user.settings::class.declaredMemberProperties.find { it.name == property.name }

                if (userSettingsProp != null) {
                    // Make the property accessible for reflection
                    userSettingsProp.isAccessible = true

                    // Set the value to the corresponding property in user.settings
                    (userSettingsProp as kotlin.reflect.KMutableProperty<*>).setter.call(user.settings, value)
                }
            }
        }

        return userRepository.save(user)
    }
}