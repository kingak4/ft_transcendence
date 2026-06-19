package code.users.ports.in;

import code.users.domain.model.UserId;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UpdateAvatarUseCase {

  @PreAuthorize(
      "hasRole(T(code.users.domain.model.Role).ADMIN.name) or @ownershipValidator.isSameUser(authentication, #userId)")
  void updateAvatar(UserId userId, UpdateAvatarCommand command);

  record UpdateAvatarCommand(String originalFilename, byte[] content) {}
}
