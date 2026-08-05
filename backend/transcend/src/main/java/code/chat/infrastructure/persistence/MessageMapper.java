package code.chat.infrastructure.persistence;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.Message;
import code.chat.domain.model.MessageId;
import code.chat.domain.model.UserId;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
  public Message toDomain(MessageEntity entity) {
    if (entity == null) return null;
    return Message.builder()
        .id(MessageId.of(entity.getId().val()))
        .chatId(ChatId.of(entity.getChatId().val()))
        .senderId(UserId.of(entity.getSenderId()))
        .content(entity.getContent())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  public MessageEntity toEntity(Message message) {
    if (message == null) return null;
    MessageEntity entity = new MessageEntity();
    entity.setId(new MessageIdEntity(message.getId().val()));
    entity.setChatId(new ChatIdEntity(message.getChatId().val()));
    entity.setSenderId(message.getSenderId().val());
    entity.setContent(message.getContent());
    entity.setCreatedAt(message.getCreatedAt());
    return entity;
  }
}
