package code.users.bootstrap

import code.bootstrap.DotEnvInitializer
import code.users.domain.model.Role
import code.users.domain.model.User
import code.users.domain.model.UserFixtures.aDaoUser
import code.users.infrastructure.persistence.UserEntityMapperImpl
import code.users.infrastructure.persistence.UserRepository
import code.users.infrastructure.security.OwnershipValidator
import code.users.ports.out.UserDao
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor

@DataJpaTest
@EnableMethodSecurity
@ActiveProfiles("test")
@ContextConfiguration(initializers = [DotEnvInitializer::class])
@Import(
  UserRepository::class,
  UserEntityMapperImpl::class,
  DefaultAvatarInitializer::class,
  OwnershipValidator::class,
  MethodValidationPostProcessor::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class UserDaoTestSupport : BehaviorSpec() {

  @Autowired
  lateinit var userDao: UserDao

  fun setupAuth(user: User) {
    val authorities = listOf(SimpleGrantedAuthority(Role.USER.name))

    val authentication = UsernamePasswordAuthenticationToken(
      user.id,
      authorities
    )
    SecurityContextHolder.getContext().authentication = authentication
  }

  init {
    extension(SpringExtension)

    beforeSpec {
      setupAuth(aDaoUser())
      userDao.createUser(aDaoUser())
    }
    afterSpec {
      SecurityContextHolder.clearContext()
    }
  }
}