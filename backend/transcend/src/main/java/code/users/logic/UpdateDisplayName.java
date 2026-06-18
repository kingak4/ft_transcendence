package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateDisplayNameUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateDisplayName implements UpdateDisplayNameUseCase {

  private final UserDao userDao;

  @Override
  public void updateDisplayName(UserId userId, UpdateDisplayNameCommand command) {
    UserDetails userDetails = userDao.findUserDetailsById(userId).orElseThrow(UserNotFoundException::new);
    UserDetails newDetails = userDetails.withDisplayName(command.displayName());
    userDao.updateDetails(userId, newDetails);
  }
}