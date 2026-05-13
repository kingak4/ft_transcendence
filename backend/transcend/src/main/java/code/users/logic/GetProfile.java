package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetProfile implements GetProfileUseCase {

  private final UserDao userDao;

  @Override
  public UserDetails getDetails(UserId userId) {
    User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
    return user.getDetails();
  }

  @Override
  public byte[] getAvatar(UserId userId) {
    if (userDao.findById(userId).isEmpty()) {
      throw new UserNotFoundException();
    }
    return userDao.getAvatar(userId);
  }
}
