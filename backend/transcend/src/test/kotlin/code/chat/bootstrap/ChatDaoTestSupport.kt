package code.chat.bootstrap

import code.bootstrap.DotEnvInitializer
import code.chat.infrastructure.persistence.ChatRepository
import code.chat.ports.out.ChatDao
import code.users.bootstrap.DefaultAvatarInitializer
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = [DotEnvInitializer::class])
@Import(
  ChatRepository::class,
  DefaultAvatarInitializer::class,
  MethodValidationPostProcessor::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ChatDaoTestSupport : BehaviorSpec() {

  @Autowired
  lateinit var chatDao: ChatDao

  init {
    extension(SpringExtension)
    beforeSpec {
      // init user fixtures etc.
    }
  }
}