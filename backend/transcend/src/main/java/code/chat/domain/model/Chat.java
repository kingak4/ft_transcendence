package code.chat.domain.model;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Chat {
  @EqualsAndHashCode.Include ChatId id;
  Set<UserId> participants;
  List<Message> messages;
}
