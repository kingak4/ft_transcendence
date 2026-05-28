package code.chat.domain.model;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Message {
  @EqualsAndHashCode.Include MessageId id;
  UserId senderId;
  String content;

  OffsetDateTime createdAt;
}
