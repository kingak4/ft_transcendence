package code.users.domain.model;

import java.util.UUID;
import org.jspecify.annotations.NonNull;

public record UserId(UUID val) {
  public static UserId of(UUID val) {
    return new UserId(val);
  }

  public static UserId generate() {
    return UserId.of(UUID.randomUUID());
  }

  @Override
  public @NonNull String toString() {
    return val.toString();
  }
}
