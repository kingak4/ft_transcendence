package code.users.logic;

import static code.users.domain.model.UserFixtures.ID_FIXTURE;
import static code.users.domain.model.UserFixtures.aDefaultUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.User;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateDisplayNameUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase.UpdateDisplayNameCommand;
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

@SpringJUnitConfig(UpdateDisplayNameTest.UpdateDisplayNameTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class UpdateDisplayNameTest {

  @Configuration
  @Import(UpdateDisplayName.class)
  static class UpdateDisplayNameTestConfig {}

  private final UpdateDisplayNameUseCase service;

  @MockitoBean private UserDao userDao;

  @Test
  void updatesDisplayNameSuccessfully() {
    // given
    var userId = new UserId(ID_FIXTURE);
    var user = aDefaultUser();
    when(userDao.findById(userId)).thenReturn(Optional.of(user));

    var command = new UpdateDisplayNameCommand("New Name");

    // when
    service.updateDisplayName(userId, command);

    // then
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userDao).updateUser(captor.capture());

    var savedUser = captor.getValue();
    assertThat(savedUser.getDetails().getDisplayName()).isEqualTo("New Name");
  }

  @Test
  void throwsUserNotFoundException() {
    // given
    var userId = new UserId(ID_FIXTURE);
    var command = new UpdateDisplayNameCommand("New Name");

    when(userDao.findById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> service.updateDisplayName(userId, command));
  }
}
