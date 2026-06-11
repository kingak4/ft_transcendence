package code.users.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class UserDetails {
  public static final AvatarId DEFAULT_AVATAR_ID =
      AvatarId.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));

  String displayName;
  AvatarId avatarId;
}
