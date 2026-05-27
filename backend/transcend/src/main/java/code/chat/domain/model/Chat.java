package code.chat.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Set;

@Value
@With
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Chat {
    @EqualsAndHashCode.Include
    ChatId id;
    Set<UserId> participants;
    List<Message> messages;
}
