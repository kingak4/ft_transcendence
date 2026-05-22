package code.users.domain.model;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Session {
  SessionId id;
  UserId userId;
  String deviceInfo;
  OffsetDateTime createdAt;
}
