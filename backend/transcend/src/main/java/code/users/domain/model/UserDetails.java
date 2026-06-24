package code.users.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class UserDetails {
  public static final String DEFAULT_DISPLAY_NAME = "User";
  ;
  String displayName;
  AvatarId avatarId;
}
