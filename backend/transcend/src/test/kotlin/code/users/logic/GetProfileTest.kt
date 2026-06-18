package code.users.logic

import code.shared.config.DaoTestSupport
import code.users.domain.exceptions.UserNotFoundException
import code.users.domain.model.AvatarId
import code.users.domain.model.UserFixtures.DISPLAY_NAME_FIXTURE
import code.users.domain.model.UserFixtures.NON_EXISTENT_USER
import code.users.domain.model.UserFixtures.USER_ID_FIXTURE
import code.users.domain.model.UserId
import code.users.infrastructure.persistence.UserEntityMapperImpl
import code.users.infrastructure.persistence.UserRepository
import code.users.ports.`in`.GetProfileUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Import(
  UserRepository::class,
  UserEntityMapperImpl::class,
  GetProfile::class
)
@Transactional
class GetProfileTest(
  private val service: GetProfileUseCase
) : DaoTestSupport() {

  init {

    Given("an existing user in the database") {
      When("the get details service is executed with the user's ID") {
        val result = service.getDetails(USER_ID_FIXTURE)

        Then("it should return the correct user details") {
          result.shouldNotBeNull()
          result.displayName shouldBe DISPLAY_NAME_FIXTURE
          result.avatarId shouldBe AvatarId.DEFAULT_AVATAR_ID
        }
      }
    }

    Given("a non-existent user ID") {

      When("the get details service is executed") {
        Then("it should throw a UserNotFoundException") {
          shouldThrow<UserNotFoundException> {
            service.getDetails(NON_EXISTENT_USER)
          }
        }
      }
    }

    Given("an existing default avatar in the database") {
      When("the get avatar service is executed") {
        val result = service.getAvatar(AvatarId.DEFAULT_AVATAR_ID)

        Then("it should return the avatar content successfully") {
          result.shouldNotBeNull()
          result.content().shouldNotBeNull()

          result.content().isNotEmpty() shouldBe true
        }
      }
    }
  }
}