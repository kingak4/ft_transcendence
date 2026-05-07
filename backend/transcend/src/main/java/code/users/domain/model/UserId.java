package code.users.domain.model;

import java.util.UUID;
import lombok.Value;

@Value
public class UserId {
  UUID val;

  public static UserId of(UUID val) {
    return new UserId(val);
  }

  public static UserId generate() {
    return new UserId(UUID.randomUUID());
  }
}
