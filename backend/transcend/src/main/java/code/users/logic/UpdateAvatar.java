package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.ProfilePhoto;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.out.UserDao;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAvatar implements UpdateAvatarUseCase {

  private final UserDao userDao;

  @Override
  public void updateAvatar(UserId userId, UpdateAvatarCommand command) {
    
  }
}
