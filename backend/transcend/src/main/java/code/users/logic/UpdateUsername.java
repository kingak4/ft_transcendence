package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.User;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateUsernameUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUsername implements UpdateUsernameUseCase {

  private final UserDao userDao;

  @Override
  public void updateUsername(UserId userId, UpdateUsernameCommand command) {
    User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
    userDao.updateUser(user.withDetails(user.getDetails().withUsername(command.username())));
  }
}