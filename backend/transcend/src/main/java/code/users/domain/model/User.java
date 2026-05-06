package code.users.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@With
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  @EqualsAndHashCode.Include
  @With
  UserId id;

  @With
  String email;

  @With
  String password;

  @With
  UserDetails details;
}