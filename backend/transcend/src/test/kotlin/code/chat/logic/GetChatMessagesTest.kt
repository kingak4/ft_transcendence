package code.chat.logic

import code.chat.bootstrap.ChatDaoTestSupport
import code.chat.domain.exception.ChatNotFoundException
import code.chat.domain.model.ChatId
import code.chat.domain.model.ChatUserFixtures.*
import code.chat.ports.`in`.GetChatMessagesUseCase
import code.users.domain.model.Role
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Ignored
import io.kotest.matchers.collections.shouldHaveSize
import java.util.*
import org.springframework.context.annotation.Import
import org.springframework.security.access.AccessDeniedException

@Ignored
@Import(GetChatMessages::class)
class GetChatMessagesTest(private val service: GetChatMessagesUseCase) : ChatDaoTestSupport() {

  init {

    afterContainer { clearAuthentication() }

    Given("a chat exists between User and Member 1") {
      And("the authenticated user is a participant (User 1)") {
        authenticateAs(CHAT_USER_ID_FIXTURE, Role.USER)

        When("requesting the chat messages") {
          val result = service.getChatMessages(chatId, 0, 10)

          Then("it should return all 5 messages") { result shouldHaveSize 5 }
        }

        When("requesting messages with pagination (size 2)") {
          val result = service.getChatMessages(chatId, 0, 2)

          Then("it should return only the first 2 messages") { result shouldHaveSize 2 }
        }
      }

      And("the authenticated user is NOT a participant (Member 2)") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.USER)

        When("requesting the chat messages") {
          Then("it should throw AccessDeniedException due to security rules") {
            shouldThrow<AccessDeniedException> { service.getChatMessages(chatId, 0, 10) }
          }
        }
      }

      And("the authenticated user is an ADMIN (not a participant)") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, role = Role.ADMIN)

        When("requesting the chat messages") {
          val result = service.getChatMessages(chatId, 0, 10)

          Then("it should bypass membership check and return the messages") {
            result shouldHaveSize 5
          }
        }
      }
    }

    Given("a non-existent chat ID") {
      val nonExistentChatId = ChatId.of(UUID.randomUUID())

      And("an admin attempts to access it") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.ADMIN)

        When("requesting messages") {
          service.getChatMessages(nonExistentChatId, 0, 10)

          Then("it should throw ChatNotFoundException") {
            shouldThrow<ChatNotFoundException> { service.getChatMessages(nonExistentChatId, 0, 10) }
          }
        }
      }
    }
  }
}
