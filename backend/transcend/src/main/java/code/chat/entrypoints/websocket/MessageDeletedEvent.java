package code.chat.entrypoints.websocket;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.MessageId;
import code.chat.domain.model.UserId;

public record MessageDeletedEvent(ChatId chatId, UserId senderId, MessageId messageId) {}
