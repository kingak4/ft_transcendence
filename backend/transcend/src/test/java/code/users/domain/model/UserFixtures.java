package code.users.domain.model;

import java.util.UUID;

public class UserFixtures {
  public static final UUID ID_FIXTURE = UUID.randomUUID();
  public static final String EMAIL_FIXTURE = "kinga@42.fr";
  public static final String PASSWORD_FIXTURE = "password-fixture";
  public static final String HASH_FIXTURE = "hash-fixture";
  public static final String TOKEN_FIXTURE = "token-fixture";
  public static final String USERNAME_FIXTURE = "Kinga";

  public static User.UserBuilder aUser() {
    return User.builder()
        .id(ID_FIXTURE)
        .email(EMAIL_FIXTURE)
        .password(HASH_FIXTURE)
        .details(new UserDetails(USERNAME_FIXTURE));
  }

  public static User aDefaultUser() {
    return aUser().build();
  }
}
