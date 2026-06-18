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

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = [DotEnvInitializer::class])
@Import(DefaultAvatarInitializer::class, UserFixtureInitializer::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class DaoTestSupport : BehaviorSpec() {

  @Autowired
  lateinit var userDao: UserDao

  init {
    extension(SpringExtension)

    beforeSpec {
      val existingUser = aDaoUser()
      userDao.createUser(existingUser)
    }
  }
}