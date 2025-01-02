package md.edit.services.account.data

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class UserSettings(

    @Enumerated(EnumType.STRING)
    var theme: Theme? = null,

    @Enumerated(EnumType.STRING)
    var headerType: HeaderType? = null

) {
    companion object {
        val DEFAULT
            get() = UserSettings(
                theme = Theme.DARK,
                headerType = HeaderType.AUTO
            )
    }
}

enum class HeaderType {
    AUTO, WIDE, NARROW
}

enum class Theme {
    LIGHT, DARK
}