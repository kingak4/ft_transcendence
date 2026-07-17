package code.chat.domain.model;

import code.users.domain.model.AvatarId;
import code.users.domain.model.Role;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import java.util.UUID;

public class ChatUserFixtures {
  public static final UserId USER_ID_FIXTURE = UserId.generate();
  public static final UUID CHAT_USER_UUID_FIXTURE = USER_ID_FIXTURE.val();

  public static final code.chat.domain.model.UserId CHAT_USER_ID_FIXTURE =
      code.chat.domain.model.UserId.of(CHAT_USER_UUID_FIXTURE);
  public static final String EMAIL_FIXTURE = "sandrzej@42.fr";
  public static final String HASH_FIXTURE = "hash-fixture";
  public static final String DISPLAY_NAME_FIXTURE = "Szymon";

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

  public static User aChatDaoUser() {
    return aTestUserBuilder().details(aTestUserDetailsBuilder().build()).build();
  }

  public static final UserId MEMBER1_ID_FIXTURE = UserId.generate();
  public static final UUID MEMBER1_UUID_FIXTURE = MEMBER1_ID_FIXTURE.val();
  public static final code.chat.domain.model.UserId CHAT_MEMBER1_ID_FIXTURE =
      code.chat.domain.model.UserId.of(MEMBER1_UUID_FIXTURE);
  public static final String MEMBER1_NAME_FIXTURE = "Alina1";
  public static final String MEMBER1_EMAIL_FIXTURE = "alina1@42.fr";

  public static User aChatMember1DaoUser() {
    UserDetails details =
        UserDetails.builder()
            .displayName(MEMBER1_NAME_FIXTURE)
            .avatarId(AvatarId.DEFAULT_AVATAR_ID)
            .build();
    return aTestUserBuilder()
        .email(MEMBER1_EMAIL_FIXTURE)
        .id(MEMBER1_ID_FIXTURE)
        .details(details)
        .build();
  }

  public static final UserId MEMBER2_ID_FIXTURE = UserId.generate();
  public static final UUID MEMBER2_UUID_FIXTURE = MEMBER2_ID_FIXTURE.val();
  public static final code.chat.domain.model.UserId CHAT_MEMBER2_ID_FIXTURE =
      code.chat.domain.model.UserId.of(MEMBER2_UUID_FIXTURE);
  public static final String MEMBER2_NAME_FIXTURE = "Alina2";
  public static final String MEMBER2_EMAIL_FIXTURE = "alina2@42.fr";

  public static User aChatMember2DaoUser() {
    UserDetails details =
        UserDetails.builder()
            .displayName(MEMBER2_NAME_FIXTURE)
            .avatarId(AvatarId.DEFAULT_AVATAR_ID)
            .build();
    return aTestUserBuilder()
        .email(MEMBER2_EMAIL_FIXTURE)
        .id(MEMBER2_ID_FIXTURE)
        .details(details)
        .build();
  }
}
