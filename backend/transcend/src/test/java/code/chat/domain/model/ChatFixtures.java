package code.chat.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatFixtures {
  public static final UUID CHAT_ID_FIXTURE = UUID.randomUUID();
  public static final UUID MESSAGE_ID_FIXTURE = UUID.randomUUID();
  public static final UUID USER_ID_1_FIXTURE = UUID.randomUUID();
  public static final UUID USER_ID_2_FIXTURE = UUID.randomUUID();
  public static final String MESSAGE_CONTENT_FIXTURE = "Hello, this is a test message";
  public static final String TOKEN_FIXTURE = "token-fixture";

  public static ChatId chatId() {
    return ChatId.of(CHAT_ID_FIXTURE);
  }

  public static MessageId messageId() {
    return MessageId.of(MESSAGE_ID_FIXTURE);
  }

  public static UserId userId1() {
    return UserId.of(USER_ID_1_FIXTURE);
  }

  public static UserId userId2() {
    return UserId.of(USER_ID_2_FIXTURE);
  }

  public static Message aDefaultMessage() {
    return Message.builder()
        .id(messageId())
        .senderId(userId1())
        .content(MESSAGE_CONTENT_FIXTURE)
        .createdAt(OffsetDateTime.now())
        .build();
  }

  public static Chat aDefaultChat() {
    return Chat.builder()
        .id(chatId())
        .participants(java.util.Set.of(userId1(), userId2()))
        .messages(java.util.List.of(aDefaultMessage()))
        .build();
  }
}
