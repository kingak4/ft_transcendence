package code.users.logic;

import static code.users.domain.model.UserFixtures.USER_ID_FIXTURE;
import static code.users.domain.model.UserFixtures.aDaoUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.User;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.in.UpdateAvatarUseCase.UpdateAvatarCommand;
import code.users.ports.out.UserDao;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(UpdateAvatarTest.UpdateAvatarTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class UpdateAvatarTest {

  @Configuration
  @Import(UpdateAvatar.class)
  static class UpdateAvatarTestConfig {}

  private final UpdateAvatarUseCase service;

  @MockitoBean private UserDao userDao;

  @Test
  void updatesAvatarSuccessfully() {
    // given
    var user = aDaoUser();
    when(userDao.findById(USER_ID_FIXTURE)).thenReturn(Optional.of(user));

    var command = new UpdateAvatarCommand("test.png", new byte[] {1, 2, 3});

    // when
    service.updateAvatar(USER_ID_FIXTURE, command);

    // then
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userDao).updateUser(captor.capture());

    var savedUser = captor.getValue();
    assertThat(savedUser.getDetails().getAvatarId()).isNotNull();
  }

  @Test
  void throwsUserNotFoundException() {
    // given
    var command = new UpdateAvatarCommand("test.png", new byte[] {1, 2, 3});

    when(userDao.findById(USER_ID_FIXTURE)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> service.updateAvatar(USER_ID_FIXTURE, command));
  }
}