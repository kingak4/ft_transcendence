package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.ProfilePhoto;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.out.UserDao;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAvatar implements UpdateAvatarUseCase {

  private final UserDao userDao;

  @Override
  public void updateAvatar(UserId userId, UpdateAvatarCommand command) {
    User user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);
    String filename = UUID.randomUUID() + "-" + command.originalFilename();

    userDao.saveAvatar(userId, filename, command.content());

    UserDetails newDetails = user.getDetails().withPhoto(new ProfilePhoto("/avatars/" + filename));
    userDao.updateUser(user.withDetails(newDetails));
  }
}
