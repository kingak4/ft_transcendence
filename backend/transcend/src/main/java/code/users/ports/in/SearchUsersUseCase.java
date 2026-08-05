package code.users.ports.in;

import code.users.domain.model.AvatarId;
import code.users.domain.model.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SearchUsersUseCase {

  record UserSearchResult(UserId id, String displayName, AvatarId avatarId) {}

  Page<UserSearchResult> searchUsers(String query, Pageable pageable);
}
