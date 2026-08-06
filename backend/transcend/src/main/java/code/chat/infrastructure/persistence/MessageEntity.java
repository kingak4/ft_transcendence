package code.chat.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class MessageEntity {
  @EmbeddedId @EqualsAndHashCode.Include MessageIdEntity id;

  @Embedded
  @AttributeOverride(name = "val", column = @Column(name = "chat_id", nullable = false))
  private ChatIdEntity chatId;

  @Column(name = "sender_id", nullable = false)
  private UUID senderId;

  @Column(name = "content", nullable = false, columnDefinition = "text")
  private String content;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;
}
