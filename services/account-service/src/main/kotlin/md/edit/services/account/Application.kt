package md.edit.services.account

import md.edit.services.account.utils.DotenvApplicationContextInitializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	DotenvApplicationContextInitializer().initialize()
	runApplication<Application>(*args)
}
