package code.users.entrypoints.http;

import code.users.domain.model.Avatar;
import code.users.domain.model.UserId;
import code.users.entrypoints.http.mappers.UsersApiMapper;
import code.users.infrastructure.security.JwtAuthenticationFilter;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.UUID;

import static code.shared.entrypoints.UrlBuilderUtil.buildUrl;
import static code.users.domain.model.UserFixtures.AVATAR_ID_FIXTURE;
import static code.users.domain.model.UserFixtures.ID_FIXTURE;
import static code.users.entrypoints.http.AvatarController.AVATAR_ENDPOINT;
import static code.users.entrypoints.http.AvatarController.BASE_URL;
import static code.users.entrypoints.http.AvatarController.UPDATE_AVATAR_ENDPOINT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AvatarController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class AvatarControllerTest {

  private static final UUID AUTH_USER_ID = UUID.randomUUID();

  private final MockMvc mockMvc;

  @MockitoBean
  private UpdateDisplayNameUseCase updateDisplayNameUseCase;
  @MockitoBean
  private UpdateAvatarUseCase updateAvatarUseCase;
  @MockitoBean
  private GetProfileUseCase getProfileUseCase;
  @MockitoBean
  private UsersApiMapper mapper;
  @MockitoBean
  private JwtAuthenticationFilter jwtAuthenticationFilter;

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
                    BASE_URL,
                    UPDATE_AVATAR_ENDPOINT))
                .file(file)
                .principal(authentication()))
        .andExpect(status().isOk());

    // then
    verify(updateAvatarUseCase)
        .updateAvatar(
            eq(UserId.of(AUTH_USER_ID)),
            any(UpdateAvatarUseCase.UpdateAvatarCommand.class));
  }

  @Test
  void getAvatarSuccessfully() throws Exception {
    // given
    byte[] avatarBytes = "test avatar content".getBytes();
    when(getProfileUseCase.getAvatar(AVATAR_ID_FIXTURE)).thenReturn(new Avatar(AVATAR_ID_FIXTURE, avatarBytes));

    // when & then
    mockMvc
        .perform(
            get(
                buildUrl(
                    BASE_URL,
                    AVATAR_ENDPOINT,
                    ID_FIXTURE)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
        .andExpect(content().bytes(avatarBytes));
  }

  private UsernamePasswordAuthenticationToken authentication() {
    return new UsernamePasswordAuthenticationToken(AUTH_USER_ID.toString(), null);
  }
}