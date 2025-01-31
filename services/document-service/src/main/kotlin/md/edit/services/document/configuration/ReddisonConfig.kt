package md.edit.services.document.configuration

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig {

    @Value("\${edit-md.live.redis.host}")
    private lateinit var redisHost: String

    @Value("\${edit-md.live.redis.port}")
    private lateinit var redisPort: String

    @Value("\${edit-md.live.redis.password}")
    private lateinit var redisPassword: String

    @Value("\${edit-md.live.redis.database}")
    private lateinit var redisDatabase: String

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()

        config.useSingleServer().address = "redis://$redisHost:$redisPort"
        config.useSingleServer().password = redisPassword
        config.useSingleServer().database = redisDatabase.toInt()

        return Redisson.create(config);
    }
}