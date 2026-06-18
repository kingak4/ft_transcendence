package code.users.logic

import code.bootstrap.config.TokenConfig.TOKEN_TYPE
import code.shared.config.DaoTestSupport
import code.users.domain.exceptions.InvalidCredentialsException
import code.users.domain.model.AuthFixtures.TOKEN_FIXTURE
import code.users.domain.model.UserFixtures.*
import code.users.infrastructure.persistence.UserEntityMapperImpl
import code.users.infrastructure.persistence.UserRepository
import code.users.ports.`in`.LoginUseCase
import code.users.ports.`in`.LoginUseCase.LoginCommand
import code.users.ports.out.AccessTokenProvider
import code.users.ports.out.HashingService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.mockito.BDDMockito.given
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean

@Import(
  UserRepository::class,
  UserEntityMapperImpl::class,
  Login::class
)
class LoginComponentTest (
  private val service: LoginUseCase
) : DaoTestSupport() {

  @MockitoBean private lateinit var hashingService: HashingService
  @MockitoBean private lateinit var accessTokenProvider: AccessTokenProvider

  init {

    Given("an existing user in the database") {
      val existingUser = aDaoUser()
      userDao.createUser(existingUser)

      And("a login command with the correct password") {
        val command = LoginCommand(EMAIL_FIXTURE, PASSWORD_FIXTURE)

        When("the login service is executed") {
          given(hashingService.matches(PASSWORD_FIXTURE, HASH_FIXTURE)).willReturn(true)
          given(accessTokenProvider.generateToken(UUID_FIXTURE.toString())).willReturn(TOKEN_FIXTURE)

          val result = service.login(command)

          Then("it should return a valid bearer token") {
            result.accessToken() shouldBe TOKEN_FIXTURE
            result.tokenType() shouldBe TOKEN_TYPE
            result.userId() shouldBe UUID_FIXTURE.toString()
          }
        }
      }

      And("a login command with an incorrect password") {
        val command = LoginCommand(EMAIL_FIXTURE, WRONG_PASSWORD_FIXTURE)

        When("the login service is executed") {
          given(hashingService.matches(WRONG_PASSWORD_FIXTURE, HASH_FIXTURE)).willReturn(false)

          Then("it should throw an InvalidCredentialsException") {
            shouldThrow<InvalidCredentialsException> {
              service.login(command)
            }
          }
        }
      }
    }

    Given("user does not exist") {
      val command = LoginCommand("nonexistent@example.com", PASSWORD_FIXTURE)

      When("the login service is executed") {
        Then("it should throw an InvalidCredentialsException") {
          shouldThrow<InvalidCredentialsException> {
            service.login(command)
          }
        }
      }
    }
  }
}