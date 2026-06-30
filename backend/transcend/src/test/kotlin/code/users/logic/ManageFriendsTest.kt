package code.users.logic

import code.users.bootstrap.FriendDaoTestSupport
import code.users.domain.exceptions.UserNotFoundException
import code.users.domain.model.FriendFixtures.*
import code.users.domain.model.UserFixtures.USER_ID_FIXTURE
import code.users.ports.`in`.ManageFriendsUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@Import(
  ManageFriends::class
)
class ManageFriendsTest(
  private val service: ManageFriendsUseCase
) : FriendDaoTestSupport() {

  init {
    Given("a user and existing potential friends") {

      When("the add friend service is executed for friend 1") {
        service.addFriend(USER_ID_FIXTURE, FRIEND1_CLASS_ID_FIXTURE)

        Then("friend 1 should be in the user's friend list") {
          val friends = userDao.getFriendList(USER_ID_FIXTURE, 0, 10)
          friends.containsKey(FRIEND1_CLASS_ID_FIXTURE) shouldBe true
        }
      }
    }

    Given("the user is already friends with friend 2") {
      userDao.addFriend(USER_ID_FIXTURE, FRIEND2_CLASS_ID_FIXTURE)

      When("the remove friend service is executed for friend 2") {
        service.removeFriend(USER_ID_FIXTURE, FRIEND2_CLASS_ID_FIXTURE)

        Then("friend 2 should be removed from the database") {
          val friends = userDao.getFriendList(USER_ID_FIXTURE, 0, 10)
          friends.containsKey(FRIEND2_CLASS_ID_FIXTURE) shouldBe false
        }
      }
    }

    Given("the system is queried for a friend list") {
      userDao.addFriend(USER_ID_FIXTURE, FRIEND1_CLASS_ID_FIXTURE)
      userDao.addFriend(USER_ID_FIXTURE, FRIEND2_CLASS_ID_FIXTURE)

      When("requesting page 0") {
        val result = service.getFriendList(USER_ID_FIXTURE, 0, 10)

        Then("it should return both friends") {
          result shouldHaveSize 2
          result[FRIEND1_CLASS_ID_FIXTURE]?.displayName shouldBe FRIEND1_NAME_FIXTURE
          result[FRIEND2_CLASS_ID_FIXTURE]?.displayName shouldBe FRIEND2_NAME_FIXTURE
        }
      }
    }

    Given("a completely random non-existent ID") {
      When("attempting to add the friend") {
        Then("it should throw UserNotFoundException") {
          shouldThrow<UserNotFoundException> {
            service.addFriend(USER_ID_FIXTURE, NON_EXISTENT_FRIEND)
          }
        }
      }
    }
  }
}