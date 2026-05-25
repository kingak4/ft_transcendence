package code.users.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import code.users.domain.model.User;
import code.users.domain.model.UserFixtures;
import code.users.ports.out.UserDao;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import code.bootstrap.DotEnvInitializer;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = DotEnvInitializer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepository.class, UserEntityMapperImpl.class})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserRepositoryTest {
  private final UserDao userRepository;

  @Test
  public void testCreateAndFindByEmail() {
    // given
    User user = UserFixtures.aDefaultUser();

    // when
    userRepository.createUser(user);
    Optional<User> found = userRepository.findByEmail(UserFixtures.EMAIL_FIXTURE);

    // then
    assertThat(found.isPresent());
    assertThat(found.get().getEmail()).isEqualTo(UserFixtures.EMAIL_FIXTURE);
  }

  @Test
  public void testFindByEmail_notFound() {
    // given
    String nonexistentEmail = "nonexistent@example.com";

    // when
    Optional<User> found = userRepository.findByEmail(nonexistentEmail);

    // then
    assertThat(found).isEmpty();
  }
}
