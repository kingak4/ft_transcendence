package code.chat.infrastructure.persistence

import code.chat.bootstrap.ChatDaoTestSupport
import code.chat.domain.model.Chat
import code.chat.domain.model.ChatFixtures
import code.chat.domain.model.ChatUserFixtures.CHAT_MEMBER1_ID_FIXTURE
import code.chat.domain.model.ChatUserFixtures.CHAT_MEMBER2_ID_FIXTURE
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@Import(ChatRepository::class)
class ChatRepositoryTest : ChatDaoTestSupport() {

  init {
    Given("two chats exist") {
      val otherChatId =
        chatDao.createChat(
          Chat.builder().participants(setOf(CHAT_MEMBER1_ID_FIXTURE, CHAT_MEMBER2_ID_FIXTURE)).build()
        )
      val message = ChatFixtures.aMessageBuilder(CHAT_MEMBER1_ID_FIXTURE).chatId(chatId).build()

      When("a message is saved for the first chat") {
        chatDao.saveMessage(message)

        Then("the message is stored in that chat, not the most recently created chat") {
          chatDao.getRecentMessages(chatId, 0, 10) shouldContain message
          chatDao.getRecentMessages(otherChatId, 0, 10).contains(message) shouldBe false
        }
      }
    }
  }
}
