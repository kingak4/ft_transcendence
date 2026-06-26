package code.chat.logic

import code.chat.bootstrap.ChatDaoTestSupport
import code.chat.domain.exception.ChatNotFoundException
import code.chat.domain.exception.EmptyMessageException
import code.chat.domain.exception.MessageNotFoundException
import code.chat.domain.exception.NotMessageOwnerException
import code.chat.domain.model.ChatId
import code.chat.domain.model.ChatUserFixtures.*
import code.chat.domain.model.MessageId
import code.chat.ports.`in`.ManageMessagesUseCase
import code.chat.ports.`in`.ManageMessagesUseCase.DeleteMessageCommand
import code.chat.ports.`in`.ManageMessagesUseCase.SendMessageCommand
import code.users.domain.model.Role
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Ignored
import io.kotest.matchers.shouldNotBe
import java.util.UUID
import org.springframework.context.annotation.Import
import org.springframework.security.access.AccessDeniedException

@Ignored
@Import(ManageMessages::class)
class ManageMessagesTest(private val service: ManageMessagesUseCase) : ChatDaoTestSupport() {

  init {

    afterContainer { clearAuthentication() }

    Given("a user wants to send a message") {
      And("the user is authenticated and sending as themselves") {
        authenticateAs(CHAT_USER_ID_FIXTURE, Role.USER)

        When("sending a valid message") {
          val command = SendMessageCommand(CHAT_USER_ID_FIXTURE, chatId, "Hello")
          val response = service.sendMessage(command)

          Then("it should return a valid SendMessageResponse") {
            response.id() shouldNotBe null
            response.createdAt() shouldNotBe null
          }
        }

        When("sending an empty message") {
          val command = SendMessageCommand(CHAT_USER_ID_FIXTURE, chatId, "   ")

          Then("it should throw EmptyMessageException") {
            shouldThrow<EmptyMessageException> { service.sendMessage(command) }
          }
        }

        When("sending to a non-existent chat") {
          val nonExistentChatId = ChatId.of(UUID.randomUUID())
          val command = SendMessageCommand(CHAT_USER_ID_FIXTURE, nonExistentChatId, "Hello")

          Then("it should throw ChatNotFoundException") {
            shouldThrow<ChatNotFoundException> { service.sendMessage(command) }
          }
        }
      }

      And("the authenticated user is different from the sender") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.USER)

        When("attempting to send a message") {
          val command = SendMessageCommand(CHAT_USER_ID_FIXTURE, chatId, "Hello")

          Then("it should throw AccessDeniedException due to security rules") {
            shouldThrow<AccessDeniedException> { service.sendMessage(command) }
          }
        }
      }

      And("an ADMIN attempts to send a message on behalf of a user") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.ADMIN)

        When("sending a message") {
          val command = SendMessageCommand(CHAT_USER_ID_FIXTURE, chatId, "Hello Admin")
          val response = service.sendMessage(command)

          Then("it should bypass ownership check and send the message") {
            response.id() shouldNotBe null
          }
        }
      }
    }

    Given("a user wants to delete a message") {
      And("the user is authenticated and deleting their own message") {
        authenticateAs(CHAT_USER_ID_FIXTURE, Role.USER)
        val commandSend = SendMessageCommand(CHAT_USER_ID_FIXTURE, chatId, "To be deleted")
        val response = service.sendMessage(commandSend)

        When("deleting the message") {
          val commandDelete = DeleteMessageCommand(CHAT_USER_ID_FIXTURE, response.id())
          service.deleteMessage(commandDelete)

          Then("it should delete successfully") {
            shouldThrow<MessageNotFoundException> { service.deleteMessage(commandDelete) }
          }
        }

        When("deleting a non-existent message") {
          val commandDelete = DeleteMessageCommand(CHAT_USER_ID_FIXTURE, MessageId.generate())

          Then("it should throw MessageNotFoundException") {
            shouldThrow<MessageNotFoundException> { service.deleteMessage(commandDelete) }
          }
        }
      }

      And("the user tries to delete a message they do not own") {
        authenticateAs(CHAT_USER_ID_FIXTURE, Role.USER)
        val commandSend = SendMessageCommand(CHAT_USER_ID_FIXTURE, chatId, "Not yours")
        val response = service.sendMessage(commandSend)

        authenticateAs(CHAT_MEMBER1_ID_FIXTURE, Role.USER)

        When("deleting the message") {
          val commandDelete = DeleteMessageCommand(CHAT_MEMBER1_ID_FIXTURE, response.id())

          Then("it should throw NotMessageOwnerException") {
            shouldThrow<NotMessageOwnerException> { service.deleteMessage(commandDelete) }
          }
        }
      }

      And("the authenticated user is different from the command sender") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.USER)

        When("attempting to delete a message") {
          val commandDelete = DeleteMessageCommand(CHAT_USER_ID_FIXTURE, MessageId.generate())

          Then("it should throw AccessDeniedException due to security rules") {
            shouldThrow<AccessDeniedException> { service.deleteMessage(commandDelete) }
          }
        }
      }

      And("an ADMIN attempts to delete a message on behalf of a user") {
        authenticateAs(CHAT_USER_ID_FIXTURE, Role.USER)
        val commandSend = SendMessageCommand(CHAT_USER_ID_FIXTURE, chatId, "Admin will delete")
        val response = service.sendMessage(commandSend)

        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.ADMIN)

        When("deleting the message") {
          val commandDelete = DeleteMessageCommand(CHAT_USER_ID_FIXTURE, response.id())
          service.deleteMessage(commandDelete)

          Then("it should bypass ownership check and delete the message") {
            shouldThrow<MessageNotFoundException> { service.deleteMessage(commandDelete) }
          }
        }
      }
    }
  }
}