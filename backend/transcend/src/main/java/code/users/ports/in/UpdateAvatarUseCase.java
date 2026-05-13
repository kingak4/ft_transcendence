package code.users.ports.in;

import code.users.domain.model.UserId;

public interface UpdateAvatarUseCase {
  
  @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSameUser(authentication, #userId)")
  void updateAvatar(UserId userId, UpdateAvatarCommand command);

  record UpdateAvatarCommand(String originalFilename, byte[] content) {}
}
