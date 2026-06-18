package code.users.logic

import code.users.bootstrap.UserDaoTestSupport
import code.users.domain.exceptions.UserNotFoundException
import code.users.domain.model.UserFixtures.*
import code.users.ports.`in`.UpdateDisplayNameUseCase
import code.users.ports.`in`.UpdateDisplayNameUseCase.UpdateDisplayNameCommand
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@Import(
  UpdateDisplayName::class
)
class UpdateDisplayNameTest(
  private val service: UpdateDisplayNameUseCase
) : UserDaoTestSupport() {

  init {

    Given("an existing user in the database") {
      val newDisplayName = "UpdatedDisplayName"
      val command = UpdateDisplayNameCommand(newDisplayName)

      When("the update display name service is executed") {
        service.updateDisplayName(USER_ID_FIXTURE, command)

        Then("it should successfully update the display name in the database") {
          val savedDetails = userDao.findUserDetailsById(USER_ID_FIXTURE).orElseThrow()

          savedDetails.displayName shouldBe newDisplayName
        }
      }
    }

    Given("a non-existent user ID for display name update") {
      val command = UpdateDisplayNameCommand(DISPLAY_NAME_FIXTURE)

      When("the update display name service is executed") {
        Then("it should throw a UserNotFoundException") {
          shouldThrow<UserNotFoundException> {
            service.updateDisplayName(NON_EXISTENT_USER, command)
          }
        }
      }
    }
  }
}