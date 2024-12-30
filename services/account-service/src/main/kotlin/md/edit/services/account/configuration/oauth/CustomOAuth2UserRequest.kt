package md.edit.services.account.configuration.oauth

data class CustomOAuth2UserRequest(
    var name: String,
    var email: String,
    var avatar: String?,
    var remoteId: String,
    var accountOrigin: String,
)
