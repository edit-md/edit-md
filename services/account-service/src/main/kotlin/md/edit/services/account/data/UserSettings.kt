package md.edit.services.account.data

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class UserSettings(
    @Enumerated(EnumType.STRING)
    var theme: Theme = Theme.DARK,

    @Enumerated(EnumType.STRING)
    var headerType: Header = Header.AUTO
)

enum class Header {
    AUTO, WIDE, NARROW
}

enum class Theme {
    LIGHT, DARK
}

//Additional settings go here
