package code.users.logic;

import code.users.ports.in.SearchUsersUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
//import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SearchUsers implements SearchUsersUseCase {

  private static final int MAX_PAGE_SIZE = 20;
  private final UserDao userDao;

  @Override
  public Page<UserSearchResult> searchUsers(String query, Pageable pageable) {
    if (pageable.getPageSize() < 0 || pageable.getPageSize() > MAX_PAGE_SIZE) {
      throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "size must be between 0 and " + MAX_PAGE_SIZE
      );
    }

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
