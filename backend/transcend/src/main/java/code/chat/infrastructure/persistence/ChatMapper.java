package code.chat.infrastructure.persistence;

import code.chat.domain.model.*;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMapper {
  private final MessageMapper messageMapper;

  public Chat toDomain(ChatEntity entity, List<MessageEntity> messages) {
    if (entity == null) return null;
    return Chat.builder()
        .id(ChatId.of(entity.getId().val()))
        .participants(
            entity.getParticipants().stream()
                .map(UserId::of)
                .collect(Collectors.toUnmodifiableSet()))
        .messages(messages.stream().map(messageMapper::toDomain).collect(Collectors.toList()))
        .build();
  }

  public ChatEntity toEntity(Chat chat) {
    if (chat == null) return null;
    ChatEntity entity = new ChatEntity();
    entity.setId(new ChatIdEntity(chat.getId().val()));
    entity.setParticipants(
        chat.getParticipants().stream()
            .map(UserId::val)
            .collect(Collectors.toCollection(HashSet::new)));
    return entity;
  }

  public List<ChatId> toChatIdList(List<ChatEntity> entities) {
    return entities.stream().map(e -> ChatId.of(e.getId().val())).toList();
  }
}
