package code.users.domain.model;

import java.util.UUID;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  @EqualsAndHashCode.Include UUID id;

  String email;
  String password;
  UserDetails details;
}