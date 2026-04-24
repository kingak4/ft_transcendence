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
    UserDetails details = UserDetails.builder().displayName(USERNAME_FIXTURE).build();
    return User.builder()
        .id(code.users.domain.model.UserId.of(ID_FIXTURE))
        .email(EMAIL_FIXTURE)
        .password(HASH_FIXTURE)
        .details(details);
  }

  public static User aDefaultUser() {
    return aUser().build();
  }
}