package md.edit.services.account.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import md.edit.services.account.data.HeaderType
import md.edit.services.account.data.Theme

class UserSettingsDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var theme: Theme? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var headerType: HeaderType? = null
}
