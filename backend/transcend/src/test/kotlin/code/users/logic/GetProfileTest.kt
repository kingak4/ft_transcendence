package code.users.logic

import code.users.bootstrap.UserDaoTestSupport
import code.users.domain.exceptions.UserNotFoundException
import code.users.domain.model.AvatarId
import code.users.domain.model.UserFixtures.*
import code.users.ports.`in`.GetProfileUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@Import(GetProfile::class)
class GetProfileTest(private val service: GetProfileUseCase) : UserDaoTestSupport() {

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
          shouldThrow<UserNotFoundException> { service.getDetails(NON_EXISTENT_USER) }
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
