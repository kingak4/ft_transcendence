<<<<<<< HEAD:backend/transcend/src/main/java/code/infrastructure/security/CustomUserDetailsService.java
package code.infrastructure.security;

import code.modules.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserDao userDao;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var user = userDao.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.email())
        .password(user.password())
        .build();
  }
}
=======
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
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var user = userDao.findByEmail(email).orElseThrow(UserNotFoundException::new);

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .build();
  }
}
>>>>>>> main:backend/transcend/src/main/java/code/users/infrastructure/security/CustomUserDetailsService.java
