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
<<<<<<< HEAD
    var user =
        userDao
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("with email: " + email));
=======
    var user = userDao.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
>>>>>>> main

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.email())
        .password(user.password())
        .build();
  }
<<<<<<< HEAD
}
=======
}
>>>>>>> main
