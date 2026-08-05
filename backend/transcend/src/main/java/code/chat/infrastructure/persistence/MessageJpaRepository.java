package code.chat.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, MessageIdEntity> {
  Page<MessageEntity> findByChatIdOrderByCreatedAtDesc(ChatIdEntity chatId, Pageable pageable);
}
