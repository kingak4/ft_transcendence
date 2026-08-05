package code.users.logic

import code.users.bootstrap.UserDaoTestSupport
import code.users.domain.model.UserFixtures.*
import code.users.ports.`in`.SearchUsersUseCase
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest

@Import(SearchUsers::class)
class SearchUsersTest(private val service: SearchUsersUseCase) : UserDaoTestSupport() {

  init {

    Given("a blank search query") {
      val pageable = PageRequest.of(0, 10)

      When("searching users with an empty string") {
        val result = service.searchUsers("", pageable)

        Then("it should return an empty page") { result.content.shouldBeEmpty() }
      }

      When("searching users with spaces") {
        val result = service.searchUsers("   ", pageable)

        Then("it should return an empty page") { result.content.shouldBeEmpty() }
      }
    }

    Given("an existing user in the database") {
      val pageable = PageRequest.of(0, 10)

      When("searching users with a matching query (full or partial match)") {
        val resultFull = service.searchUsers(DISPLAY_NAME_FIXTURE, pageable)
        val resultPartial =
          service.searchUsers(DISPLAY_NAME_FIXTURE.substring(0, 3).lowercase(), pageable)

        Then("it should return the user successfully") {
          resultFull.content.shouldNotBeEmpty()
          resultFull.content.size shouldBe 1
          resultFull.content[0].displayName shouldBe DISPLAY_NAME_FIXTURE
          resultFull.content[0].id shouldBe USER_ID_FIXTURE

          resultPartial.content.shouldNotBeEmpty()
          resultPartial.content.size shouldBe 1
          resultPartial.content[0].displayName shouldBe DISPLAY_NAME_FIXTURE
        }
      }

      When("searching users with a non-matching query") {
        val result = service.searchUsers("NonMatchingQueryTextXYZ", pageable)

        Then("it should return an empty page") { result.content.shouldBeEmpty() }
      }
    }
  }
}
