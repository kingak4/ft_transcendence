package code.users.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDetails {
  String username;
  ProfilePhoto photo;
}