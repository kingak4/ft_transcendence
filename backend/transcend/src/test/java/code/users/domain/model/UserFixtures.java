package code.users.domain.model;

import java.util.UUID;

public class UserFixtures {
  public static final UUID USER_UUID_FIXTURE = UUID.randomUUID();
  public static final UserId USER_ID_FIXTURE = UserId.of(USER_UUID_FIXTURE);
  public static final String EMAIL_FIXTURE = "kinga@42.fr";
  public static final String PASSWORD_FIXTURE = "password-fixture";
  public static final String HASH_FIXTURE = "hash-fixture";
  public static final String DISPLAY_NAME_FIXTURE = "Kinga";

  public static final String INVALID_EMAIL_FIXTURE = "invalid-email";
  public static final String WRONG_PASSWORD_FIXTURE = "wrong-password";
  public static final String NEW_EMAIL = "newuser@example.com";

  public static final UserId NON_EXISTENT_USER = UserId.of(UUID.randomUUID());

  public static User.UserBuilder aTestUserBuilder() {
    return User.builder()
        .id(USER_ID_FIXTURE)
        .email(EMAIL_FIXTURE)
        .password(HASH_FIXTURE)
        .role(Role.USER);
  }

  public static UserDetails.UserDetailsBuilder aTestUserDetailsBuilder() {
    return UserDetails.builder()
        .displayName(DISPLAY_NAME_FIXTURE)
        .avatarId(AvatarId.DEFAULT_AVATAR_ID);
  }

  public static User aDaoUser() {
    return aTestUserBuilder().details(aTestUserDetailsBuilder().build()).build();
  }

  public static User aSimpleUser() {
    UserDetails details = UserDetails.builder().displayName("").avatarId(null).build();
    return aTestUserBuilder().details(details).build();
  }
}
