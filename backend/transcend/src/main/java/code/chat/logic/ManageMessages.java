package code.chat.logic;

import code.chat.domain.exception.ChatNotFoundException;
import code.chat.domain.exception.EmptyMessageException;
import code.chat.domain.exception.MessageNotFoundException;
import code.chat.domain.exception.NotMessageOwnerException;
import code.chat.domain.model.Chat;
import code.chat.domain.model.Message;
import code.chat.domain.model.MessageId;
import code.chat.ports.in.ManageMessagesUseCase;
import code.chat.ports.out.ChatDao;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageMessages implements ManageMessagesUseCase {
  private final ChatDao dao;

  @Override
  public void sendMessage(SendMessageCommand command) {
    if (command.content().isBlank()) throw new EmptyMessageException();
    Optional<Chat> chat = dao.getChat(command.chatId());
    if (chat.isEmpty()) throw new ChatNotFoundException();
    Message message =
        Message.builder()
            .id(MessageId.generate())
            .senderId(command.sender())
            .createdAt(OffsetDateTime.now())
            .content(command.content())
            .build();
    dao.saveMessage(message);
  }

  @Override
  public void deleteMessage(DeleteMessageCommand command) {
    Optional<Message> message = dao.getMessage(command.messageId());
    if (message.isEmpty()) throw new MessageNotFoundException();
    if (!message.get().getSenderId().equals(command.sender())) throw new NotMessageOwnerException();
    dao.deleteMessage(command.messageId());
  }
}