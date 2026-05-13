package code.users.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.in.UpdateAvatarUseCase.UpdateAvatarCommand;
import code.users.ports.out.UserDao;
import java.util.Optional;
import java.util.UUID;
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
    var userId = new UserId(UUID.randomUUID());
    var user = User.builder().id(userId).details(UserDetails.builder().build()).build();
    when(userDao.findById(userId)).thenReturn(Optional.of(user));

    var command = new UpdateAvatarCommand("test.png", new byte[] {1, 2, 3});

    // when
    service.updateAvatar(userId, command);

    // then
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userDao).updateUser(captor.capture());

    var savedUser = captor.getValue();
    assertThat(savedUser.getDetails().getAvatarUrl()).isNotNull();
    assertThat(savedUser.getDetails().getAvatarUrl()).startsWith("/avatars/");
    assertThat(savedUser.getDetails().getAvatarUrl()).endsWith("test.png");
  }

  @Test
  void throwsUserNotFoundException() {
    // given
    var userId = new UserId(UUID.randomUUID());
    var command = new UpdateAvatarCommand("test.png", new byte[] {1, 2, 3});
    
    when(userDao.findById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> service.updateAvatar(userId, command));
  }
}
