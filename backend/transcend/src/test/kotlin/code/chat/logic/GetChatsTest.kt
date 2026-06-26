package code.chat.logic

import code.chat.bootstrap.ChatDaoTestSupport
import code.chat.domain.model.ChatUserFixtures.*
import code.chat.ports.`in`.GetChatsUseCase
import code.users.domain.model.Role
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Ignored
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.context.annotation.Import
import org.springframework.security.access.AccessDeniedException

@Ignored
@Import(GetChats::class)
class GetChatsTest(private val service: GetChatsUseCase) : ChatDaoTestSupport() {

  init {
    afterContainer { clearAuthentication() }

    Given("a user has an existing chat") {
      And("the authenticated user requests their own chats (User 1)") {
        authenticateAs(CHAT_USER_ID_FIXTURE, Role.USER)

        When("requesting the chat list") {
          val result = service.getChatList(CHAT_USER_ID_FIXTURE, 0, 10)

          Then("it should return the user's chats") { result shouldHaveSize 1 }
        }
      }

      And("another user (Member 2) attempts to request User 1's chats") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.USER)

        When("requesting the chat list") {
          Then("it should throw AccessDeniedException due to security rules") {
            shouldThrow<AccessDeniedException> { service.getChatList(CHAT_USER_ID_FIXTURE, 0, 10) }
          }
        }
      }

      And("an ADMIN attempts to request User 1's chats") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, role = Role.ADMIN)

        When("requesting the chat list") {
          val result = service.getChatList(CHAT_USER_ID_FIXTURE, 0, 10)

          Then("it should bypass the ownership check and return the chats") {
            result shouldHaveSize 1
          }
        }
      }
    }

    Given("a user with no chats") {
      And("the authenticated user requests their own chats (Member 2)") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.USER)

        When("requesting the chat list") {
          val result = service.getChatList(CHAT_MEMBER2_ID_FIXTURE, 0, 10)

          Then("it should return an empty list") { result shouldHaveSize 0 }
        }
      }
    }
  }
}