package code.users.logic;

import static code.users.domain.model.UserFixtures.AVATAR_NAME_FIXTURE;
import static code.users.domain.model.UserFixtures.AVATAR_URL_FIXTURE;
import static code.users.domain.model.UserFixtures.USER_ID_FIXTURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.ManageFriendsUseCase;
import code.users.ports.out.UserDao;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(ManageFriendsTest.ManageFriendsTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ManageFriendsTest {

  @Configuration
  @Import(ManageFriends.class)
  static class ManageFriendsTestConfig {}

  private final ManageFriendsUseCase service;

  @MockitoBean private UserDao userDao;

  @Test
  void addFriendSuccessfully() {
    // given
    var friendId = new UserId(UUID.randomUUID());

    // when
    service.addFriend(USER_ID_FIXTURE, friendId);

    // then
    verify(userDao).addFriend(USER_ID_FIXTURE, friendId);
  }

  @Test
  void removeFriendSuccessfully() {
    // given
    var friendId = new UserId(UUID.randomUUID());

    // when
    service.removeFriend(USER_ID_FIXTURE, friendId);

    // then
    verify(userDao).removeFriend(USER_ID_FIXTURE, friendId);
  }

  @Test
  void getFriendListSuccessfully() {
    // given
    var expectedFriends =
        Map.of(
            new FriendId(),
            UserDetails.builder()
                .displayName(AVATAR_NAME_FIXTURE)
                .avatarUrl(AVATAR_URL_FIXTURE)
                .build());
    when(userDao.getFriendList(USER_ID_FIXTURE, 0, 10)).thenReturn(expectedFriends);

    // when
    var result = service.getFriendList(USER_ID_FIXTURE, 0, 10);

    // then
    verify(userDao).getFriendList(USER_ID_FIXTURE, 0, 10);
    assertThat(result).hasSize(1);
    assertThat(result.values().iterator().next().getDisplayName()).isEqualTo(AVATAR_NAME_FIXTURE);
  }
}
