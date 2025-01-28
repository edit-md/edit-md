package md.edit.services.document

import com.aayushatharva.brotli4j.Brotli4jLoader
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
	Brotli4jLoader.ensureAvailability();

	runApplication<Application>(*args)
}
