package code.users.logic;

import static code.users.domain.model.UserFixtures.ID_FIXTURE;
import static code.users.domain.model.UserFixtures.aDefaultUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.Avatar;
import code.users.domain.model.UserId;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.out.UserDao;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(GetProfileTest.GetProfileTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class GetProfileTest {

  @Configuration
  @Import(GetProfile.class)
  static class GetProfileTestConfig {}

  private final GetProfileUseCase service;

  @MockitoBean private UserDao userDao;

  @Test
  void getDetailsSuccessfully() {
    // given
    var userId = new UserId(ID_FIXTURE);
    var user = aDefaultUser();
    when(userDao.findById(userId)).thenReturn(Optional.of(user));

    // when
    var result = service.getDetails(userId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDisplayName()).isEqualTo(user.getDetails().getDisplayName());
    assertThat(result.getAvatarUrl()).isEqualTo(user.getDetails().getAvatarUrl());
  }

  @Test
  void getDetailsThrowsUserNotFoundException() {
    // given
    var userId = new UserId(ID_FIXTURE);

    when(userDao.findById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> service.getDetails(userId));
  }
  
  @Test
  void getAvatarSuccessfully() {
    // given
    var userId = new UserId(ID_FIXTURE);
    var user = aDefaultUser();
    when(userDao.findById(userId)).thenReturn(Optional.of(user));

    var expectedAvatar = new Avatar(new byte[]{1, 2, 3});
    when(userDao.getAvatar(userId)).thenReturn(expectedAvatar);

    // when
    var result = service.getAvatar(userId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).containsExactly(new byte[]{1, 2, 3});
  }
}
