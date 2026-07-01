package code.users.logic

import code.users.bootstrap.UserDaoTestSupport
import code.users.domain.model.UserFixtures.NON_EXISTENT_USER
import code.users.domain.model.UserFixtures.USER_ID_FIXTURE
import code.users.ports.`in`.UpdateAvatarUseCase
import code.users.ports.`in`.UpdateAvatarUseCase.UpdateAvatarCommand
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import
import org.springframework.security.authorization.AuthorizationDeniedException

@Import(UpdateAvatar::class)
class UpdateAvatarTest(private val service: UpdateAvatarUseCase) : UserDaoTestSupport() {

  init {

    Given("an existing user in the database") {
      val content = byteArrayOf(1, 2, 3)
      val command = UpdateAvatarCommand("test.png", content)

      When("the update avatar service is executed") {
        service.updateAvatar(USER_ID_FIXTURE, command)

        Then("it should successfully update the user's avatar in the database") {
          val savedDetails = userDao.findUserDetailsById(USER_ID_FIXTURE).orElseThrow()
          val newAvatarId = savedDetails.avatarId

          newAvatarId.shouldNotBeNull()

          val savedAvatar = userDao.findById(newAvatarId).orElseThrow()
          savedAvatar.content() shouldBe content
        }
      }
    }

    Given("a non-existent user ID for avatar update") {
      val command = UpdateAvatarCommand("test.png", byteArrayOf(1, 2, 3))

      When("the update avatar service is executed") {
        Then("it should throw a AuthorizationDeniedException") {
          shouldThrow<AuthorizationDeniedException> {
            service.updateAvatar(NON_EXISTENT_USER, command)
          }
        }
      }
    }
  }
}
