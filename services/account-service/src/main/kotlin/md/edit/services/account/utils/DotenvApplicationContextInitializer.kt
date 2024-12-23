package md.edit.services.account.utils

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvException
import md.edit.services.account.Application
import org.springframework.context.ConfigurableApplicationContext


class DotenvApplicationContextInitializer {

    fun initialize() {
        val profile = System.getProperty("spring.profiles.active", "dev")

        if (profile == "dev") {
            // get the path of the specific service
            val path = Application::class.java.protectionDomain.codeSource.location.path.split('/').drop(1).dropLast(3)
                .joinToString("/")

            try {
                val env = Dotenv.configure()
                    .directory(path) // Set the location of your .env file
                    .load() // Load the .env file

                env.entries().forEach {
                    System.setProperty(it.key, it.value)
                }

                println("Loaded .env file from $path")
            } catch (e: DotenvException) {
                println("WARN: No .env file found in $path")
            }
        }
    }
}