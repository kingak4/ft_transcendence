package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.Avatar;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static code.users.domain.model.UserDetails.DEFAULT_AVATAR_URL;
import static code.users.domain.model.UserDetails.DEFAULT_AVATAR_USER_ID;

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
  public Avatar getAvatar(UserId userId) {
    User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
    if (user.getDetails().getAvatarUrl().equals(DEFAULT_AVATAR_URL))
      return userDao.getAvatar(DEFAULT_AVATAR_USER_ID);
    else
      return userDao.getAvatar(userId);
  }
}