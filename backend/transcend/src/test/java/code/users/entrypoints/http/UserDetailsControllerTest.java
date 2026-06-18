package code.users.entrypoints.http;

import static code.shared.entrypoints.UrlBuilderUtil.buildUrl;
import static code.users.domain.model.UserFixtures.DISPLAY_NAME_FIXTURE;
import static code.users.domain.model.UserFixtures.USER_ID_FIXTURE;
import static code.users.domain.model.UserFixtures.USER_UUID_FIXTURE;
import static code.users.domain.model.UserFixtures.aDaoUser;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.users.domain.model.AvatarId;
import code.users.domain.model.UserDetails;
import code.users.domain.model.UserFixtures;
import code.users.domain.model.UserId;
import code.users.entrypoints.http.mappers.UsersApiMapper;
import code.users.infrastructure.security.JwtAuthenticationFilter;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserDetailsController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class UserDetailsControllerTest {

  private static final UUID AUTH_USER_ID = UUID.randomUUID();

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockitoBean private UpdateDisplayNameUseCase updateDisplayNameUseCase;
  @MockitoBean private UpdateAvatarUseCase updateAvatarUseCase;
  @MockitoBean private GetProfileUseCase getProfileUseCase;
  @MockitoBean private UsersApiMapper mapper;
  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void getDetailsSuccessfully() throws Exception {
    // given
    UserDetails details = aDaoUser().getDetails();
    var response =
        new UserDetailsController.GetUserDetailsResponse(
            DISPLAY_NAME_FIXTURE, AvatarId.DEFAULT_AVATAR_ID.val());

    when(getProfileUseCase.getDetails(USER_ID_FIXTURE)).thenReturn(details);
    when(mapper.toResponse(details)).thenReturn(response);

    // when & then
    mockMvc
        .perform(
            get(
                buildUrl(
                    UserDetailsController.BASE_URL,
                    UserDetailsController.DETAILS_ENDPOINT,
                    USER_UUID_FIXTURE)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value(DISPLAY_NAME_FIXTURE))
        .andExpect(jsonPath("$.avatarId").value(AvatarId.DEFAULT_AVATAR_ID.val().toString()));
  }

  @Test
  void updateDisplayNameSuccessfully() throws Exception {
    // given
    var request =
        new UserDetailsController.UpdateDisplayNameRequest(UserFixtures.DISPLAY_NAME_FIXTURE);
    var command =
        new UpdateDisplayNameUseCase.UpdateDisplayNameCommand(UserFixtures.DISPLAY_NAME_FIXTURE);

    when(mapper.toCommand(request)).thenReturn(command);

    // when & then
    mockMvc
        .perform(
            patch(
                    buildUrl(
                        UserDetailsController.BASE_URL,
                        UserDetailsController.UPDATE_DISPLAY_NAME_ENDPOINT))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(authentication()))
        .andExpect(status().isOk());

    verify(updateDisplayNameUseCase).updateDisplayName(UserId.of(AUTH_USER_ID), command);
  }

  private UsernamePasswordAuthenticationToken authentication() {
    return new UsernamePasswordAuthenticationToken(AUTH_USER_ID.toString(), null);
  }
}
