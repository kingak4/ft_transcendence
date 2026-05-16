package code.users.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class UserDetails {
  public static final String AVATARS_BASE_URL = "/avatars/";
  public static final String DEFAULT_AVATAR_URL = AVATARS_BASE_URL + "default.png";
  public static final UserId DEFAULT_AVATAR_USER_ID = new UserId(new UUID(0, 0));

  String displayName;
  String avatarUrl;
  @Builder.Default boolean online = false;
}
