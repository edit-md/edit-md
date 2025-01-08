package md.edit.services.account.services

import md.edit.services.account.configuration.oauth.CustomOAuth2UserRequest
import md.edit.services.account.data.*
import md.edit.services.account.exceptions.AuthorizationException
import md.edit.services.account.exceptions.UserNotFoundException
import md.edit.services.account.repos.ConnectedAccountRepository
import md.edit.services.account.repos.UserRepository
import md.edit.services.account.utils.AuthorizationUtils
import org.springframework.security.core.Authentication
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
     * @param request OAuth2UserRequest
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
            .orElseThrow { UserNotFoundException() }
    }

    /**
     * Gets a user by Authentication
     * @param authentication Authentication
     * @return User
     */

    @Transactional
    fun getUser(authentication: Authentication): User {
        val authUser = AuthorizationUtils.onlyUser(authentication)
        return userRepository.findById(authUser.id).map { it }.orElseThrow { UserNotFoundException() }
    }

    /**
     * Gets a user by id
     * @param id User id
     * @return User
     */
    @Transactional
    fun getUserById(authentication: Authentication, id: UUID): User {
        val user = userRepository.findById(id).orElseThrow { UserNotFoundException() }

        // Allow API access without further checks
        if (AuthorizationUtils.isAPI(authentication)) return user

        return user
    }

    @Transactional
    fun searchNames(authentication: Authentication, searchTerm: String): Collection<User> {
        if (AuthorizationUtils.isAPI(authentication))
            throw AuthorizationException()
        return userRepository.findUsersByName(searchTerm)
    }

    @Transactional
    fun updateUserSettings(authentication: Authentication, theme: Theme?, headerType: HeaderType?): User {
        val user: User = getUser(authentication)

        //TODO PROPER HANDLING
        val newUserSettings = UserSettings(theme, headerType)

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