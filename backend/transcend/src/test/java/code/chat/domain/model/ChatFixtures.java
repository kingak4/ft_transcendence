package code.chat.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatFixtures {
  public static final ChatId CHAT_ID_FIXTURE = ChatId.generate();
  public static final UUID CHAT_UUID_FIXTURE = CHAT_ID_FIXTURE.val();

  public static final String MESSAGE_CONTENT_FIXTURE = "Hello, this is a test message from %s";
  public static final String TOKEN_FIXTURE = "token-fixture";

  public static Message aDefaultMessage() {
    return aMessageBuilder(ChatUserFixtures.CHAT_USER_ID_FIXTURE).build();
  }

  public static Message.MessageBuilder aMessageBuilder(UserId sender) {
    String filler = sender.val().toString();
    return Message.builder()
        .id(MessageId.generate())
        .senderId(sender)
        .content(MESSAGE_CONTENT_FIXTURE.formatted(filler))
        .createdAt(OffsetDateTime.now());
  }
}
