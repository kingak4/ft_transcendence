package code.shared.config

import code.bootstrap.DotEnvInitializer
import code.users.bootstrap.DefaultAvatarInitializer
import code.users.bootstrap.UserFixtureInitializer
import code.users.domain.model.UserFixtures.aDaoUser
import code.users.ports.out.UserDao
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
  DefaultAvatarInitializer::class,
  MethodValidationPostProcessor::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DaoTestSupport : BehaviorSpec() {

  @Autowired
  lateinit var userDao: UserDao

  init {
    extension(SpringExtension)

    beforeSpec {
      userDao.createUser(aDaoUser())
    }
  }
}