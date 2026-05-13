package code.users.logic;

import static code.users.domain.model.UserFixtures.ID_FIXTURE;
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
    var userId = new UserId(ID_FIXTURE);
    var friendId = new UserId(UUID.randomUUID());

    // when
    service.addFriend(userId, friendId);

    // then
    verify(userDao).addFriend(userId, friendId);
  }

  @Test
  void removeFriendSuccessfully() {
    // given
    var userId = new UserId(ID_FIXTURE);
    var friendId = new UserId(UUID.randomUUID());

    // when
    service.removeFriend(userId, friendId);

    // then
    verify(userDao).removeFriend(userId, friendId);
  }

  @Test
  void getFriendListSuccessfully() {
    // given
    var userId = new UserId(ID_FIXTURE);
    var expectedFriends = Map.of(
        new FriendId(), UserDetails.builder().displayName("Friend 1").avatarUrl("/av1").online(true).build()
    );
    when(userDao.getFriendList(userId, 0, 10)).thenReturn(expectedFriends);

    // when
    var result = service.getFriendList(userId, 0, 10);

    // then
    verify(userDao).getFriendList(userId, 0, 10);
    assertThat(result).hasSize(1);
    assertThat(result.values().iterator().next().getDisplayName()).isEqualTo("Friend 1");
  }
}