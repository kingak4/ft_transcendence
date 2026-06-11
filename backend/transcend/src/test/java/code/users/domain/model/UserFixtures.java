package code.users.domain.model;

import java.util.UUID;

public class UserFixtures {
  public static final UUID ID_FIXTURE = UUID.randomUUID();
  public static final UUID FRIEND_ID_FIXTURE = UUID.randomUUID();
  public static final String SESSION_FIXTURE = UUID.randomUUID().toString();
  public static final String EMAIL_FIXTURE = "kinga@42.fr";
  public static final String PASSWORD_FIXTURE = "password-fixture";
  public static final String HASH_FIXTURE = "hash-fixture";
  public static final String TOKEN_FIXTURE = "token-fixture";
  public static final String DISPLAY_NAME_FIXTURE = "Kinga";
  public static final UserId USER_ID_FIXTURE = UserId.of(ID_FIXTURE);
  public static final AvatarId AVATAR_ID_FIXTURE = AvatarId.generate();
  public static final String AVATAR_NAME_FIXTURE = "Friend 1";
  public static final String NAME_FIXTURE = "NAME_FIXTURE";
  public static final String WRONG_PASSWORD_FIXTURE = "wrong-password";
  public static final String INVALID_EMAIL_FIXTURE = "invalid-email";

  public static User.UserBuilder aDefaultUserBuilder() {
    UserDetails details =
        UserDetails.builder().displayName(DISPLAY_NAME_FIXTURE).avatarId(AVATAR_ID_FIXTURE).build();
    return User.builder()
        .id(USER_ID_FIXTURE)
        .email(EMAIL_FIXTURE)
        .password(HASH_FIXTURE)
        .role(Role.USER)
        .details(details);
  }

  public static User.UserBuilder aSimpleUserBuilder() {
    UserDetails details = UserDetails.builder().displayName("").avatarId(null).build();
    return User.builder()
        .id(USER_ID_FIXTURE)
        .email(EMAIL_FIXTURE)
        .password(HASH_FIXTURE)
        .role(Role.USER)
        .details(details);
  }

  public static User aSimpleUser() {
    return aSimpleUserBuilder().build();
  }

  public static User aDefaultUser() {
    return aDefaultUserBuilder().build();
  }

  public static User aFriendUser() {
    return aSimpleUserBuilder().email("korzecho@42.fr").id(UserId.of(FRIEND_ID_FIXTURE)).build();
  }
}
