package code.users.entrypoints.http;

import static code.shared.entrypoints.UrlBuilderUtil.buildUrl;
import static code.users.domain.model.UserFixtures.aDaoUser;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.users.domain.model.FriendId;
import code.users.domain.model.UserDetails;
import code.users.infrastructure.security.JwtAuthenticationFilter;
import code.users.ports.in.ManageFriendsUseCase;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {FriendsController.class, FriendsPaginationController.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class FriendsControllerTest {

  private static final UUID AUTH_USER_ID = UUID.randomUUID();

  private final MockMvc mockMvc;

  @MockitoBean private ManageFriendsUseCase manageFriendsUseCase;
  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void addFriendSuccessfully() throws Exception {
    // given
    UUID friendId = UUID.randomUUID();

    // when & then
    mockMvc
        .perform(
            post(buildUrl(
                    FriendsController.FRIENDS_ENDPOINT,
                    FriendsController.FRIEND_ENDPOINT,
                    friendId))
                .principal(authentication()))
        .andExpect(status().isCreated());

    verify(manageFriendsUseCase)
        .addFriend(code.users.domain.model.UserId.of(AUTH_USER_ID), FriendId.of(friendId));
  }

  @Test
  void removeFriendSuccessfully() throws Exception {
    // given
    UUID friendId = UUID.randomUUID();

    // when & then
    mockMvc
        .perform(
            delete(
                    buildUrl(
                        FriendsController.FRIENDS_ENDPOINT,
                        FriendsController.FRIEND_ENDPOINT,
                        friendId))
                .principal(authentication()))
        .andExpect(status().isNoContent());

    verify(manageFriendsUseCase)
        .removeFriend(code.users.domain.model.UserId.of(AUTH_USER_ID), FriendId.of(friendId));
  }

  @Test
  void getFriendListSuccessfully() throws Exception {
    // given
    FriendId friendId = FriendId.of(UUID.randomUUID());
    UserDetails friendDetails = aDaoUser().getDetails();

    Map<FriendId, UserDetails> friendsMap = Map.of(friendId, friendDetails);

    when(manageFriendsUseCase.getFriendList(code.users.domain.model.UserId.of(AUTH_USER_ID), 0, 10))
        .thenReturn(friendsMap);

    // when & then
    mockMvc
        .perform(
            get(buildUrl(FriendsController.FRIENDS_ENDPOINT, null))
                .param("page", "0")
                .param("size", "10")
                .principal(authentication()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*").exists());
  }

  private UsernamePasswordAuthenticationToken authentication() {
    return new UsernamePasswordAuthenticationToken(AUTH_USER_ID.toString(), null);
  }
}