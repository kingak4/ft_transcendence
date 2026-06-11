package code.users.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.UUID;

@With
@Value
@Builder
public class UserDetails {
  public static final AvatarId DEFAULT_AVATAR_ID = AvatarId.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));

  String displayName;
  AvatarId avatarId;
}