package code.users.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.UUID;

public record FriendId(UUID val) {
  public static FriendId of(UUID val) {
    return new FriendId(val);
  }

  @JsonValue
  public UUID val() {
    return val;
  }
}
