package code.chat.entrypoints.websocket;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.MessageId;
import code.chat.domain.model.UserId;
import java.time.OffsetDateTime;

public record MessageSentEvent(
    ChatId chatId, UserId senderId, MessageId messageId, String content, OffsetDateTime time) {}
