package code.users.domain.model;

import java.util.UUID;
import lombok.Value;

@Value
public class UserId {
  UUID value;

  public static UserId of(UUID value) {
    return new UserId(value);
  }

  public static UserId generate() {
    return new UserId(UUID.randomUUID());
  }
}
