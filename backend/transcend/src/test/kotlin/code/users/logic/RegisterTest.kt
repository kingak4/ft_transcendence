package code.users.logic

import code.users.bootstrap.UserDaoTestSupport
import code.users.domain.exceptions.EmailAlreadyRegisteredException
import code.users.domain.model.UserFixtures.*
import code.users.ports.`in`.RegisterUseCase
import code.users.ports.out.HashingService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.validation.ConstraintViolationException
import org.mockito.BDDMockito.given
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean

@Import(
  Register::class
)
class RegisterComponentTest(
  private val service: RegisterUseCase
) : UserDaoTestSupport() {

  @MockitoBean
  private lateinit var hashingService: HashingService

  init {
    Given("a valid register command for a new user") {
      val command = RegisterUseCase.RegisterCommand(NEW_EMAIL, PASSWORD_FIXTURE)

      When("the register service is executed") {
        given(hashingService.encode(PASSWORD_FIXTURE)).willReturn(HASH_FIXTURE)

        val result = service.register(command)

        Then("it should create a user and return a valid ID") {
          result.id().shouldNotBeNull()
          val savedUser = userDao.findByEmail(NEW_EMAIL)
          savedUser.isPresent shouldBe true
        }
      }
    }

    Given("an existing user in the database") {
      And("a register command with the same email") {
        val command = RegisterUseCase.RegisterCommand(EMAIL_FIXTURE, PASSWORD_FIXTURE)

        When("the register service is executed") {
          Then("it should throw an EmailAlreadyRegisteredException") {
            shouldThrow<EmailAlreadyRegisteredException> {
              service.register(command)
            }
          }
        }
      }
    }

    Given("an invalid register command") {
      val command = RegisterUseCase.RegisterCommand(INVALID_EMAIL_FIXTURE, WRONG_PASSWORD_FIXTURE)

      When("the register service is executed") {
        Then("it should throw a ConstraintViolationException") {
          shouldThrow<ConstraintViolationException> {
            service.register(command)
          }
        }
      }
    }
  }
}