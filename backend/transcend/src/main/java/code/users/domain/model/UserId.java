package code.users.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.UUID;
import org.jspecify.annotations.NonNull;

public record UserId(UUID val) {
  public static UserId of(UUID val) {
    return new UserId(val);
  }

  public static UserId generate() {
    return UserId.of(UUID.randomUUID());
  }

  @JsonValue
  public UUID val() {
    return val;
  }

  @Override
  public @NonNull String toString() {
    return val.toString();
  }
}
