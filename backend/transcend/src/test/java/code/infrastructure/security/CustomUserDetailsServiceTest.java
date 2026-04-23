package code.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.modules.users.domain.User;
import code.modules.users.ports.out.UserDao;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ContextConfiguration(
    classes = CustomUserDetailsServiceTest.CustomUserDetailsServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class CustomUserDetailsServiceTest {

  @MockBean private UserDao userDao;

  private final UserDetailsService userDetailsService;

  @Configuration
  @Import(CustomUserDetailsService.class)
  static class CustomUserDetailsServiceTestConfig {}

  @Test
  void loadUserByUsernameReturnsUserDetailsWhenUserExists() {
    // given
    var email = "john@example.com";
    var encodedPassword = "encoded-password";
    var user = new User(UUID.randomUUID(), email, encodedPassword);
    when(userDao.findByEmail(email)).thenReturn(Optional.of(user));

    // when
    var userDetails = userDetailsService.loadUserByUsername(email);

    // then
    assertEquals(email, userDetails.getUsername());
    assertEquals(encodedPassword, userDetails.getPassword());
    verify(userDao).findByEmail(email);
  }

  @Test
  void loadUserByUsernameThrowsWhenUserDoesNotExist() {
    // given
    var email = "missing@example.com";
    when(userDao.findByEmail(email)).thenReturn(Optional.empty());

    // when
    var exception =
        assertThrows(
            UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));

    // then
    assertEquals("with email: " + email, exception.getMessage());
    verify(userDao).findByEmail(email);
  }
}
