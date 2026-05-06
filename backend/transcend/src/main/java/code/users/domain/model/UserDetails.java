package code.users.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

@Value
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PUBLIC)
@With
public class UserDetails {

  String displayName;
  ProfilePhoto profilePhoto;
}