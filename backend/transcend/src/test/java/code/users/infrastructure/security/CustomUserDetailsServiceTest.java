package code.users.infrastructure.security;

import static code.users.domain.model.UserFixtures.EMAIL_FIXTURE;
import static code.users.domain.model.UserFixtures.HASH_FIXTURE;
import static code.users.domain.model.UserFixtures.ID_FIXTURE;
import static code.users.domain.model.UserFixtures.aDefaultUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.ports.out.UserDao;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(CustomUserDetailsServiceTest.CustomUserDetailsServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class CustomUserDetailsServiceTest {

  @Configuration
  @Import(CustomUserDetailsService.class)
  static class CustomUserDetailsServiceTestConfig {}

  private final UserDetailsService userDetailsService;

  @MockitoBean private UserDao userDao;

  @Test
  void loadUserByUsernameReturnsUserDetailsWhenUserExists() {
    // given
    var user = aDefaultUser();
    String idStr = user.getId().getVal().toString();
    when(userDao.findById(user.getId())).thenReturn(Optional.of(user));

    // when
    var userDetails = userDetailsService.loadUserByUsername(idStr);

    // then
    assertEquals(idStr, userDetails.getUsername());
    assertEquals(HASH_FIXTURE, userDetails.getPassword());
    verify(userDao).findById(user.getId());
  }

  @Test
  void loadUserByUsernameThrowsWhenUserDoesNotExist() {
    // given
    String idStr = ID_FIXTURE.toString();
    var userId = code.users.domain.model.UserId.of(ID_FIXTURE);
    when(userDao.findById(userId)).thenReturn(Optional.empty());

    // when
    var exception =
        assertThrows(
            UserNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(idStr));

    // then
    assertEquals(UserNotFoundException.MESSAGE, exception.getMessage());
    verify(userDao).findById(userId);
  }
}
