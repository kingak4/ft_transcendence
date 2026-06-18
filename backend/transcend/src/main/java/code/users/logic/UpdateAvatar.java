package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAvatar implements UpdateAvatarUseCase {

  private final UserDao userDao;

  @Override
  public void updateAvatar(UserId userId, UpdateAvatarCommand command) {
    UserDetails userDetails = userDao.findUserDetailsById(userId).orElseThrow(UserNotFoundException::new);

    AvatarId avatarId = AvatarId.generate();
    userDao.saveAvatar(new Avatar(avatarId, command.content()));

    UserDetails newDetails = userDetails.withAvatarId(avatarId);
    userDao.updateDetails(userId, newDetails);
  }
}