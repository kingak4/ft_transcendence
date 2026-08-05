package code.chat.domain.model;

import io.micrometer.common.lang.NonNull;
import java.util.UUID;

public record UserId(UUID val) {
  public static UserId of(UUID val) {
    return new UserId(val);
  }

  @Override
  public @NonNull String toString() {
    return val.toString();
  }
}
