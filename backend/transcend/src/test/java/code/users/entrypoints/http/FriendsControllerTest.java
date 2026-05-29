package code.users.entrypoints.http;

import static code.users.domain.model.UserFixtures.ID_FIXTURE;
import static code.users.domain.model.UserFixtures.USER_ID_FIXTURE;
import static code.users.domain.model.UserFixtures.aDefaultUser;
import static code.users.entrypoints.http.UrlBuilderUtil.buildUrl;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FriendsController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class FriendsControllerTest {

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
            post(
                buildUrl(
                    FriendsController.FRIENDS_ENDPOINT,
                    FriendsController.FRIEND_ENDPOINT,
                    ID_FIXTURE,
                    friendId)))
        .andExpect(status().isCreated());

    verify(manageFriendsUseCase).addFriend(USER_ID_FIXTURE, FriendId.of(friendId));
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
                    ID_FIXTURE,
                    friendId)))
        .andExpect(status().isNoContent());

    verify(manageFriendsUseCase).removeFriend(USER_ID_FIXTURE, FriendId.of(friendId));
  }

  @Test
  void getFriendListSuccessfully() throws Exception {
    // given
    FriendId friendId = FriendId.of(UUID.randomUUID());
    UserDetails friendDetails = aDefaultUser().getDetails();

    Map<FriendId, UserDetails> friendsMap = Map.of(friendId, friendDetails);

    when(manageFriendsUseCase.getFriendList(USER_ID_FIXTURE, 0, 10)).thenReturn(friendsMap);

    // when & then
    mockMvc
        .perform(
            get(buildUrl(FriendsController.FRIENDS_ENDPOINT, null, ID_FIXTURE))
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*").exists());
  }
}
