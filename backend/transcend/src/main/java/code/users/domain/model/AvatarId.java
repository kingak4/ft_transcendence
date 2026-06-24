package code.users.domain.model;

import java.util.UUID;

public record AvatarId(UUID val) {
  public static final AvatarId DEFAULT_AVATAR_ID =
      AvatarId.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));

  public static AvatarId of(UUID val) {
    return new AvatarId(val);
  }

  public static AvatarId generate() {
    return AvatarId.of(UUID.randomUUID());
  }
}
