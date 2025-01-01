package md.edit.services.account.data

data class UserSettings(var theme: Theme = Theme.DARK, var headerType: Header = Header.AUTO)

enum class Header {
    AUTO, WIDE, NARROW
}

enum class Theme {
    LIGHT, DARK
}

//Additional settings go here
