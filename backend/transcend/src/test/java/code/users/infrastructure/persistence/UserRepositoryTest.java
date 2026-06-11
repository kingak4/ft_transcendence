package code.users.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import code.bootstrap.DotEnvInitializer;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.Avatar;
import code.users.domain.model.FriendId;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import java.util.Map;
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

  @Test
  void testFindById() {
    // given
    User user = UserFixtures.aDefaultUser();
    userRepository.createUser(user);

    // when
    Optional<User> found = userRepository.findById(user.getId());

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo(UserFixtures.EMAIL_FIXTURE);
  }

  @Test
  void testUpdateUser_updatesPasswordAndDetails() {
    // given
    User user = UserFixtures.aDefaultUser();
    userRepository.createUser(user);

    User updated = user.withPassword("new-password");
    updated = updated.withDetails(
            new UserDetails("UpdatedName", null)
    );

    // when
    userRepository.updateUser(updated);

    // then
    Optional<User> found = userRepository.findById(user.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getPassword()).isEqualTo("new-password");
    assertThat(found.get().getDetails().getDisplayName()).isEqualTo("UpdatedName");
  }

  @Test
  void testSaveAvatarAndGetAvatar() {
    // given
    User user = UserFixtures.aDefaultUser();
    userRepository.createUser(user);

    Avatar avatar = new Avatar("avatar-content");

    // when
    userRepository.saveAvatar(user.getId(), avatar);
    Avatar result = userRepository.getAvatar(user.getId());

    // then
    assertThat(result.content()).isEqualTo("avatar-content");
  }

  @Test
  void testGetAvatar_notFound() {
    // given
    User user = UserFixtures.aDefaultUser();
    userRepository.createUser(user);

    // then
    assertThatThrownBy(() ->
            userRepository.getAvatar(user.getId())
    ).isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  void testAddFriend_andExists() {
    // given
    User user = UserFixtures.aDefaultUser();
    userRepository.createUser(user);

    FriendId friendId = FriendId.of(UUID.randomUUID());

    // when
    userRepository.addFriend(user.getId(), friendId);

    // then
    assertThat(userRepository.exists(friendId)).isTrue();
  }

  @Test
  void testRemoveFriend() {
    // given
    User user = UserFixtures.aDefaultUser();
    userRepository.createUser(user);

    FriendId friendId = FriendId.of(UUID.randomUUID());

    userRepository.addFriend(user.getId(), friendId);

    // when
    userRepository.removeFriend(user.getId(), friendId);

    // then
    assertThat(userRepository.exists(friendId)).isFalse();
  }

  @Test
  void testGetFriendList_pagination() {
    // given
    User user = UserFixtures.aDefaultUser();
    userRepository.createUser(user);

    FriendId f1 = FriendId.of(UUID.randomUUID());
    FriendId f2 = FriendId.of(UUID.randomUUID());
    FriendId f3 = FriendId.of(UUID.randomUUID());

    userRepository.addFriend(user.getId(), f1);
    userRepository.addFriend(user.getId(), f2);
    userRepository.addFriend(user.getId(), f3);

    // when
    Map<FriendId, UserDetails> page = userRepository.getFriendList(user.getId(), 0, 2);

    // then
    assertThat(page).hasSize(2);
  }
}
