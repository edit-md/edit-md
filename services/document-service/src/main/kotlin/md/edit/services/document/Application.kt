package md.edit.services.document

import org.redisson.spring.starter.RedissonAutoConfigurationV2
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
	exclude = [RedissonAutoConfigurationV2::class]
)
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
