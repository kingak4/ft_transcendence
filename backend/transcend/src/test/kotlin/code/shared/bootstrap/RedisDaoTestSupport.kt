package code.shared.bootstrap

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestType
import io.kotest.extensions.spring.SpringExtension
import java.net.ServerSocket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.data.redis.connection.RedisConnectionFactory
import redis.embedded.RedisServer

@DataRedisTest
abstract class RedisDaoTestSupport : BehaviorSpec() {

  @Autowired private lateinit var connectionFactory: RedisConnectionFactory

  init {
    extension(SpringExtension)

    beforeTest {
      if (it.type == TestType.Test) {
        connectionFactory.connection.serverCommands().flushAll()
      }
    }
  }

  companion object {
    private val redisServer: RedisServer
    private val redisPort: Int

    init {
      val socket = ServerSocket(0)
      redisPort = socket.localPort
      socket.close()

      redisServer = RedisServer(redisPort)
      redisServer.start()

      System.setProperty("spring.data.redis.port", redisPort.toString())
      System.setProperty("spring.data.redis.host", "localhost")

      Runtime.getRuntime().addShutdownHook(Thread { redisServer.stop() })
    }
  }
}
