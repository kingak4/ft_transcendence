package code.users.ports.in;

import code.users.domain.model.UserId;

public interface UpdateAvatarUseCase {
  void updateAvatar(UserId userId, UpdateAvatarCommand command);

  record UpdateAvatarCommand(String originalFilename, byte[] content) {}
}
