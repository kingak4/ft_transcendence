package code.chat.ports.in;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.MessageId;
import code.chat.domain.model.UserId;

public interface ManageMessagesUseCase {
    void sendMessage(SendMessageCommand command);
    void deleteMessage(DeleteMessageCommand command);

    record SendMessageCommand(UserId sender, ChatId chatId, String content) {}

    record DeleteMessageCommand(UserId sender, MessageId messageId) {}
}
