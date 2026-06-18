package code.chat.bootstrap

import code.bootstrap.DotEnvInitializer
import code.chat.infrastructure.persistence.ChatRepository
import code.chat.logic.MembershipValidator
import code.chat.ports.out.ChatDao
import code.users.bootstrap.DefaultAvatarInitializer
import code.users.ports.out.UserDao
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor

@DataJpaTest
@ActiveProfiles("test")
@EnableMethodSecurity
@ContextConfiguration(initializers = [DotEnvInitializer::class])
@Import(
  ChatRepository::class,
  DefaultAvatarInitializer::class,
  MembershipValidator::class,
  MethodValidationPostProcessor::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ChatDaoTestSupport : BehaviorSpec() {

  @Autowired
  lateinit var chatDao: ChatDao

  @MockitoBean
  lateinit var userDao: UserDao

  init {
    extension(SpringExtension)
    beforeSpec {
      // init user fixtures etc.
    }
  }
}