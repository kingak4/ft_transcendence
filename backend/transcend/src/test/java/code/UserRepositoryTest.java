package code;

import code.users.domain.model.User;
import code.users.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import code.users.domain.model.Role;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserId;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import code.users.infrastructure.persistence.UserEntityMapperImpl;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserRepository.class, UserEntityMapperImpl.class})
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateAndFindByEmail() {
        User user = User.builder()
                .id(UserId.of(UUID.randomUUID()))
                .email("test@email.com")
                .password("secret")
                .role(Role.USER)
                .details(UserDetails.builder()
                        .displayName("testuser")
                        .avatarUrl(UserDetails.DEFAULT_AVATAR_URL)
                        .build())
                .build();

        userRepository.createUser(user);

        Optional<User> found = userRepository.findByEmail("test@email.com");
        assertThat(found.get().getEmail()).isEqualTo("test@email.com");
    }

    @Test
    public void testFindByEmail_notFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }
}
