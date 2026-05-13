package code.users.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class UserDetails {
  String displayName;
  String avatarUrl;
  @Builder.Default boolean online = false;
}