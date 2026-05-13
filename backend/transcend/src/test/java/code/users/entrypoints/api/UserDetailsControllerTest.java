package code.users.entrypoints.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.users.domain.model.UserId;
import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.infrastructure.security.JwtAuthenticationFilter;
import code.users.ports.in.UpdateAvatarUseCase;
import code.users.ports.in.UpdateDisplayNameUseCase;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserDetailsController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserDetailsControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UpdateDisplayNameUseCase updateDisplayNameUseCase;
  @MockitoBean private UpdateAvatarUseCase updateAvatarUseCase;
  @MockitoBean private UsersApiMapper mapper;
  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void uploadAvatarSuccessfully() throws Exception {
    UUID userId = UUID.randomUUID();
    MockMultipartFile file =
        new MockMultipartFile("file", "test.png", "image/png", "test image".getBytes());

    mockMvc
        .perform(multipart("/users/" + userId + "/avatar").file(file))
        .andExpect(status().isOk());

    verify(updateAvatarUseCase)
        .updateAvatar(eq(new UserId(userId)), any(UpdateAvatarUseCase.UpdateAvatarCommand.class));
  }
}
