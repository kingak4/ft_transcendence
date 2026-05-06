package code.users.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PUBLIC)
@With
public class UserDetails {

  String displayName;
  ProfilePhoto profilePhoto;
}
