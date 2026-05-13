package code.users.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateAvatarUseCase.UpdateAvatarCommand;
import code.users.ports.out.UserDao;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UpdateAvatarTest {

  private UserDao userDao;
  private UpdateAvatar updateAvatar;
  private UserId userId;

  @BeforeEach
  void setUp() {
    userDao = mock(UserDao.class);
    updateAvatar = new UpdateAvatar(userDao);
    userId = new UserId(UUID.randomUUID());
  }

  @Test
  void updatesAvatarSuccessfully() {
    User user = User.builder().id(userId).details(UserDetails.builder().build()).build();
    when(userDao.findById(userId)).thenReturn(Optional.of(user));

    UpdateAvatarCommand command = new UpdateAvatarCommand("test.png", new byte[] {1, 2, 3});
    updateAvatar.updateAvatar(userId, command);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userDao).updateUser(captor.capture());

    User savedUser = captor.getValue();
    assertThat(savedUser.getDetails().getAvatarUrl()).isNotNull();
    assertThat(savedUser.getDetails().getAvatarUrl()).startsWith("/avatars/");
    assertThat(savedUser.getDetails().getAvatarUrl()).endsWith("test.png");
  }

  @Test
  void throwsUserNotFoundException() {
    when(userDao.findById(userId)).thenReturn(Optional.empty());
    UpdateAvatarCommand command = new UpdateAvatarCommand("test.png", new byte[] {1, 2, 3});
    assertThrows(UserNotFoundException.class, () -> updateAvatar.updateAvatar(userId, command));
  }
}
