package code.users.logic;

import code.users.domain.model.UserId;
import code.users.ports.in.SearchUsersUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchUsers implements SearchUsersUseCase {

  private final UserDao userDao;

  @Override
  public Page<UserSearchResult> searchUsers(String query, Pageable pageable) {
    if (query == null || query.trim().isEmpty()) {
      return Page.empty();
    }
    return userDao
        .searchUsers(query.trim(), pageable)
        .map(
            user ->
                new UserSearchResult(
                    user.getId(),
                    user.getDetails().getDisplayName(),
                    user.getDetails().getAvatarId()));
  }
}
