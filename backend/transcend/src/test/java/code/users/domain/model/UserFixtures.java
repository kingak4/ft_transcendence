package code.users.domain.model;

import java.util.UUID;

public class UserFixtures {
  public static final UUID ID_FIXTURE = UUID.randomUUID();
  public static final String EMAIL_FIXTURE = "kinga@42.fr";
  public static final String PASSWORD_FIXTURE = "password-fixture";
  public static final String HASH_FIXTURE = "hash-fixture";
  public static final String TOKEN_FIXTURE = "token-fixture";
  public static final String DISPLAY_NAME_FIXTURE = "Kinga";
  public static final UserId USER_ID_FIXTURE = new UserId(ID_FIXTURE);
  public static final String AVATAR_URL_FIXTURE = "/avatars/1";
  public static final String AVATAR_NAME_FIXTURE = "Friend 1";
  public static final String NAME_FIXTURE = "NAME_FIXTURE";
  public static final String WRONG_PASSWORD_FIXTURE = "wrong-password";
  public static final String INVALID_EMAIL_FIXTURE = "invalid-email";

  public static User.UserBuilder aUser() {
    UserDetails details =
        UserDetails.builder()
            .displayName(DISPLAY_NAME_FIXTURE)
            .avatarUrl(AVATAR_URL_FIXTURE)
            .build();
    return User.builder()
        .id(USER_ID_FIXTURE)
        .email(EMAIL_FIXTURE)
        .password(HASH_FIXTURE)
        .details(details);
  }

  public static User aDefaultUser() {
    return aUser().build();
  }
}
