package code.chat.logic

import code.chat.bootstrap.ChatDaoTestSupport
import code.chat.ports.`in`.GetChatMessagesUseCase
import org.springframework.context.annotation.Import

@Import(
  GetChatMessages::class
)
class GetChatMessagesTest(
  private val service: GetChatMessagesUseCase
) : ChatDaoTestSupport() {

  init {
  }
}