package code.users.domain.model;

import static code.users.domain.model.UserFixtures.aTestUserBuilder;

import java.util.UUID;

public class FriendFixtures {
  public static final UUID FRIEND1_UUID_FIXTURE = UUID.randomUUID();
  public static final UserId FRIEND1_ID_FIXTURE = UserId.of(FRIEND1_UUID_FIXTURE);
  public static final String FRIEND1_NAME_FIXTURE = "Kacper";
  public static final String FRIEND1_EMAIL_FIXTURE = "korzecho@42.fr";

  public static final FriendId NON_EXISTENT_FRIEND = FriendId.of(UUID.randomUUID());

  public static User aFriend1DaoUser() {
    UserDetails details =
        UserDetails.builder()
            .displayName(FRIEND1_NAME_FIXTURE)
            .avatarId(AvatarId.DEFAULT_AVATAR_ID)
            .build();
    return aTestUserBuilder()
        .email(FRIEND1_EMAIL_FIXTURE)
        .id(FRIEND1_ID_FIXTURE)
        .details(details)
        .build();
  }

  public static final UUID FRIEND2_UUID_FIXTURE = UUID.randomUUID();
  public static final UserId FRIEND2_ID_FIXTURE = UserId.of(FRIEND2_UUID_FIXTURE);
  public static final String FRIEND2_NAME_FIXTURE = "Kacper2";
  public static final String FRIEND2_EMAIL_FIXTURE = "korzecho2@42.fr";

  public static User aFriend2DaoUser() {
    UserDetails details =
        UserDetails.builder()
            .displayName(FRIEND2_NAME_FIXTURE)
            .avatarId(AvatarId.DEFAULT_AVATAR_ID)
            .build();
    return aTestUserBuilder()
        .email(FRIEND2_EMAIL_FIXTURE)
        .id(FRIEND2_ID_FIXTURE)
        .details(details)
        .build();
  }

  public static final FriendId FRIEND1_CLASS_ID_FIXTURE =
      FriendId.of(FriendFixtures.FRIEND1_UUID_FIXTURE);
  public static final FriendId FRIEND2_CLASS_ID_FIXTURE =
      FriendId.of(FriendFixtures.FRIEND2_UUID_FIXTURE);
}
