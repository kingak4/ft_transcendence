package code.users.logic;

import code.users.domain.exceptions.UserNotFoundException;
import code.users.domain.model.Avatar;
import code.users.domain.model.UserDetails;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static code.users.domain.model.UserFixtures.AVATAR_ID_FIXTURE;
import static code.users.domain.model.UserFixtures.USER_ID_FIXTURE;
import static code.users.domain.model.UserFixtures.aDefaultUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(GetProfileTest.GetProfileTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class GetProfileTest {

  @Configuration
  @Import(GetProfile.class)
  static class GetProfileTestConfig {}

  private final GetProfileUseCase service;

  @MockitoBean private UserDao userDao;

  @Test
  void getDetailsSuccessfully() {
    // given
    UserDetails details = aDefaultUser().getDetails();
    when(userDao.findUserDetailsById(USER_ID_FIXTURE)).thenReturn(Optional.of(details));

    // when
    var result = service.getDetails(USER_ID_FIXTURE);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDisplayName()).isEqualTo(details.getDisplayName());
    assertThat(result.getAvatarId()).isEqualTo(details.getAvatarId());
  }

  @Test
  void getDetailsThrowsUserNotFoundException() {
    // given

    when(userDao.findById(USER_ID_FIXTURE)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> service.getDetails(USER_ID_FIXTURE));
  }

  @Test
  void getAvatarSuccessfully() {
    // given
    var user = aDefaultUser();
    UserDetails details = aDefaultUser().getDetails();
    when(userDao.findUserDetailsById(USER_ID_FIXTURE)).thenReturn(Optional.of(details));

    var expectedAvatar = new Avatar(AVATAR_ID_FIXTURE, new byte[] {1, 2, 3});
    when(userDao.findById(AVATAR_ID_FIXTURE)).thenReturn(expectedAvatar);

    // when
    var result = service.getAvatar(USER_ID_FIXTURE);

    // then
    assertThat(result).isNotNull();
    assertThat(result.content()).containsExactly(new byte[] {1, 2, 3});
  }
}