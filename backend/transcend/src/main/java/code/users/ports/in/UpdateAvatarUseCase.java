package code.users.ports.in;

import code.users.domain.model.UserId;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UpdateAvatarUseCase {

  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSameUser(authentication, #userId)")
  void updateAvatar(UserId userId, UpdateAvatarCommand command);

  record UpdateAvatarCommand(String originalFilename, byte[] content) {}
}
