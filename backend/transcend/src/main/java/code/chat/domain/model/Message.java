package code.chat.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;

import java.time.OffsetDateTime;

@Value
@With
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Message {
    @EqualsAndHashCode.Include
    MessageId id;
    UserId senderId;

    OffsetDateTime createdAt;
}
