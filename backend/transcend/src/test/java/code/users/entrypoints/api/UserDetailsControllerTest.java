package code.users.entrypoints.api;

import static code.users.domain.model.UserFixtures.ID_FIXTURE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.users.domain.model.UserId;
import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.infrastructure.security.JwtAuthenticationFilter;
import code.users.ports.in.GetProfileUseCase;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserDetailsController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class UserDetailsControllerTest {

  private final MockMvc mockMvc;

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
        .perform(multipart("/users/" + ID_FIXTURE + "/avatar").file(file))
        .andExpect(status().isOk());

    // then
    verify(updateAvatarUseCase)
        .updateAvatar(
            eq(new UserId(ID_FIXTURE)), any(UpdateAvatarUseCase.UpdateAvatarCommand.class));
  }
}
