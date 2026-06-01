package code.users.domain.model;

import java.util.UUID;

public record FriendId(UUID val) {
  public static FriendId of(UUID val) {
    return new FriendId(val);
  }
}
