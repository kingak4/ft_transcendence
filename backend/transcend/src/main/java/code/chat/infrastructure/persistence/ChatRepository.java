package code.chat.infrastructure.persistence;

import code.chat.domain.model.*;
import code.chat.ports.out.ChatDao;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class ChatRepository implements ChatDao {
  private final ChatJpaRepository chatJpaRepository;
  private final MessageJpaRepository messageJpaRepository;
  private final ChatMapper chatMapper;
  private final MessageMapper messageMapper;

  @Override
  public Optional<ChatId> findChat(UserId initiator, UserId recipient) {
    return chatJpaRepository
            .findByParticipants(initiator.val(), recipient.val())
            .map(entity -> ChatId.of(entity.getId().val()));
  }

  @Override
  public List<ChatId> getChatList(UserId userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return chatJpaRepository
            .findAllByParticipant(userId.val(), pageable)
            .map(entity -> ChatId.of(entity.getId().val()))
            .toList();
  }

  @Override
  public Optional<Chat> getChat(ChatId chatId) {
    ChatIdEntity id = new ChatIdEntity(chatId.val());
    return chatJpaRepository
            .findById(id)
            .map(
                chatEntity -> {
                  List<MessageEntity> messages =
                          messageJpaRepository
                                  .findByChatIdOrderByCreatedAtDesc(id, Pageable.unpaged())
                                  .getContent();
                  return chatMapper.toDomain(chatEntity, messages);
                });
  }

  @Override
  public List<Message> getRecentMessages(ChatId chatId, int page, int size) {
    ChatIdEntity id = new ChatIdEntity(chatId.val());
    Pageable pageable = PageRequest.of(page, size);
    return messageJpaRepository.findByChatIdOrderByCreatedAtDesc(id, pageable).stream()
            .map(messageMapper::toDomain)
            .toList();
  }

  @Override
  public ChatId createChat(Chat chat) {
    ChatEntity entity = chatMapper.toEntity(chat);
    ChatEntity saved = chatJpaRepository.save(entity);
    return ChatId.of(saved.getId().val());
  }

  @Override
  public void saveMessage(Message message) {
    messageJpaRepository.save(messageMapper.toEntity(message));
  }

  @Override
  public Optional<Message> getMessage(MessageId id) {
    return messageJpaRepository
            .findById(new MessageIdEntity(id.val()))
            .map(messageMapper::toDomain);
  }

  @Override
  public void deleteMessage(MessageId messageId) {
    messageJpaRepository.deleteById(new MessageIdEntity(messageId.val()));
  }
}
