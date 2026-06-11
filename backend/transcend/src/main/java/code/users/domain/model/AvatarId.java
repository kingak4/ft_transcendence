package code.users.domain.model;

import java.util.UUID;

public record AvatarId(UUID val) {
  public static AvatarId of(UUID val) {
    return new AvatarId(val);
  }
  public static AvatarId generate() {
    return AvatarId.of(UUID.randomUUID());
  }
}