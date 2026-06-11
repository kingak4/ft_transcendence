package code.users.infrastructure.persistence;

import code.bootstrap.DotEnvInitializer;
import code.users.domain.model.Avatar;
import code.users.domain.model.AvatarId;
import code.users.domain.model.FriendId;
import code.users.domain.model.User;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserFixtures;
import code.users.ports.out.UserDao;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
    assertThat(found).isPresent();
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

    String newPassword = "new-password";
    String newName = "UpdatedName";
    User updated = user.withPassword(newPassword);
    updated = updated.withDetails(
        UserDetails.builder()
            .displayName(newName)
            .build()
    );

    // when
    userRepository.updateUser(updated);

    // then
    Optional<User> found = userRepository.findById(user.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getPassword()).isEqualTo(newPassword);
    assertThat(found.get().getDetails().getDisplayName()).isEqualTo(newName);
  }

  @Test
  void testSaveAvatarAndGetAvatar() {
    // given
    User user = UserFixtures.aDefaultUser();
    userRepository.createUser(user);

    AvatarId id = AvatarId.generate();
    Avatar avatar = new Avatar(id, "avatar-content".getBytes());

    // when
    userRepository.saveAvatar(avatar);
    Optional<Avatar> byId = userRepository.findById(id);

    // then
    assertThat(byId.isPresent());
    assertThat(byId.get().content()).isEqualTo("avatar-content".getBytes());
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
  @Disabled
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
  @Disabled
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