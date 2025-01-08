package md.edit.services.account.converters

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import md.edit.services.account.data.UserSettings

@Converter(autoApply = true)
class UserSettingsConverter : AttributeConverter<UserSettings, String> {

    private val objectMapper = ObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    }

    override fun convertToDatabaseColumn(attribute: UserSettings?): String {
        return try {
            objectMapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            throw IllegalArgumentException("Error serializing UserSettings to JSON", e)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): UserSettings {
        return try {
            objectMapper.readValue(dbData, UserSettings::class.java)
        } catch (e: Exception) {
            throw IllegalArgumentException("Error deserializing JSON to UserSettings", e)
        }
    }
}