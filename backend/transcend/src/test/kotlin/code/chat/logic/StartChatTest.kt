package code.chat.logic

import code.chat.bootstrap.ChatDaoTestSupport
import code.chat.domain.model.ChatUserFixtures.*
import code.chat.ports.`in`.StartChatUseCase
import code.chat.ports.`in`.StartChatUseCase.StartChatCommand
import code.users.domain.model.Role
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Ignored
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.context.annotation.Import
import org.springframework.security.access.AccessDeniedException

@Ignored
@Import(StartChat::class)
class StartChatTest(private val service: StartChatUseCase) : ChatDaoTestSupport() {

  init {

    afterContainer { clearAuthentication() }

    Given("a user wants to start a chat") {
      And("the user is authenticated and starting a chat as themselves (initiator)") {
        authenticateAs(CHAT_USER_ID_FIXTURE, Role.USER)

        When("starting a chat with a new recipient") {
          val command = StartChatCommand(CHAT_USER_ID_FIXTURE, CHAT_MEMBER2_ID_FIXTURE)
          val result = service.startChat(command)

          Then("it should create and return a valid new ChatId") { result shouldNotBe null }
        }

        When("starting a chat with a recipient they already have a chat with") {
          val command = StartChatCommand(CHAT_USER_ID_FIXTURE, CHAT_MEMBER1_ID_FIXTURE)
          val result = service.startChat(command)

          Then("it should return the existing ChatId") { result shouldBe chatId }
        }
      }

      And("the authenticated user is different from the initiator") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.USER)

        When("attempting to start a chat on behalf of another user") {
          val command = StartChatCommand(CHAT_USER_ID_FIXTURE, CHAT_MEMBER1_ID_FIXTURE)

          Then("it should throw AccessDeniedException due to security rules") {
            shouldThrow<AccessDeniedException> { service.startChat(command) }
          }
        }
      }

      And("an ADMIN attempts to start a chat on behalf of a user") {
        authenticateAs(CHAT_MEMBER2_ID_FIXTURE, Role.ADMIN)

        When("starting a chat") {
          val command = StartChatCommand(CHAT_USER_ID_FIXTURE, CHAT_MEMBER2_ID_FIXTURE)
          val result = service.startChat(command)

          Then("it should bypass the ownership check and return a ChatId") {
            result shouldNotBe null
          }
        }
      }
    }
  }
}