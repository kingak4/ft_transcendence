package code.users.entrypoints.http;

import static code.shared.entrypoints.UrlBuilderUtil.buildUrl;
import static code.users.domain.model.UserFixtures.AVATAR_ID_FIXTURE;
import static code.users.domain.model.UserFixtures.DISPLAY_NAME_FIXTURE;
import static code.users.domain.model.UserFixtures.ID_FIXTURE;
import static code.users.domain.model.UserFixtures.NAME_FIXTURE;
import static code.users.domain.model.UserFixtures.USER_ID_FIXTURE;
import static code.users.domain.model.UserFixtures.aDefaultUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.users.domain.model.Avatar;
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
import org.springframework.mock.web.MockMultipartFile;
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
  void uploadAvatarSuccessfully() throws Exception {
    // given
    MockMultipartFile file =
        new MockMultipartFile("file", "test.png", "image/png", "test image".getBytes());

    // when
    mockMvc
        .perform(
            multipart(
                    buildUrl(
                        UserDetailsController.BASE_URL,
                        UserDetailsController.UPDATE_AVATAR_ENDPOINT))
                .file(file)
                .principal(authentication()))
        .andExpect(status().isOk());

    // then
    verify(updateAvatarUseCase)
        .updateAvatar(
            eq(code.users.domain.model.UserId.of(AUTH_USER_ID)),
            any(UpdateAvatarUseCase.UpdateAvatarCommand.class));
  }

  @Test
  void getAvatarSuccessfully() throws Exception {
    // given
    byte[] avatarBytes = "test avatar content".getBytes();
    when(getProfileUseCase.getAvatar(USER_ID_FIXTURE)).thenReturn(new Avatar(AVATAR_ID_FIXTURE, avatarBytes));

    // when & then
    mockMvc
        .perform(
            get(
                buildUrl(
                    UserDetailsController.BASE_URL,
                    UserDetailsController.AVATAR_ENDPOINT,
                    ID_FIXTURE)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
        .andExpect(content().bytes(avatarBytes));
  }

  @Test
  void getDetailsSuccessfully() throws Exception {
    // given
    UserDetails details = aDefaultUser().getDetails();
    var response =
        new UserDetailsController.GetUserDetailsResponse(DISPLAY_NAME_FIXTURE, AVATAR_ID_FIXTURE.val());

    when(getProfileUseCase.getDetails(USER_ID_FIXTURE)).thenReturn(details);
    when(mapper.toResponse(details)).thenReturn(response);

    // when & then
    mockMvc
        .perform(
            get(
                buildUrl(
                    UserDetailsController.BASE_URL,
                    UserDetailsController.DETAILS_ENDPOINT,
                    ID_FIXTURE)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value(DISPLAY_NAME_FIXTURE))
        .andExpect(jsonPath("$.avatarId").value(AVATAR_ID_FIXTURE.val().toString()));
  }

  @Test
  void updateDisplayNameSuccessfully() throws Exception {
    // given
    var request = new UserDetailsController.UpdateDisplayNameRequest(NAME_FIXTURE);
    var command = new UpdateDisplayNameUseCase.UpdateDisplayNameCommand(NAME_FIXTURE);

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

    verify(updateDisplayNameUseCase)
        .updateDisplayName(UserId.of(AUTH_USER_ID), command);
  }

  private UsernamePasswordAuthenticationToken authentication() {
    return new UsernamePasswordAuthenticationToken(AUTH_USER_ID.toString(), null);
  }
}