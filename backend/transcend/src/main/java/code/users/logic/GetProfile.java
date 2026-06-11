package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.Avatar;
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
    return userDao.findUserDetailsById(userId).orElseThrow(UserNotFoundException::new);
  }

  @Override
  public Avatar getAvatar(UserId userId) {
    UserDetails userDetails = userDao.findUserDetailsById(userId).orElseThrow(UserNotFoundException::new);
    return userDao.findById(userDetails.getAvatarId());
  }
}