package code.users.infrastructure.security;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CustomUserDetailsService implements UserDetailsService {

  private final UserDao userDao;

  @Override
  public UserDetails loadUserByUsername(String idStr) throws UsernameNotFoundException {
    var user =
        userDao
            .findById(code.users.domain.model.UserId.of(java.util.UUID.fromString(idStr)))
            .orElseThrow(UserNotFoundException::new);
    String roleName = user.getRole() != null ? user.getRole().name() : "USER";

    return org.springframework.security.core.userdetails.User.builder()
        .username(idStr)
        .password(user.getPassword())
        .roles(roleName)
        .build();
  }
}
