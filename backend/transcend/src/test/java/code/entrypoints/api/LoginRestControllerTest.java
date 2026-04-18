package code.entrypoints.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.infrastructure.security.JwtAuthenticationFilter;
import code.modules.users.ports.in.LoginUser;
import code.modules.users.ports.in.LoginUser.LoginCommand;
import code.modules.users.ports.in.LoginUser.LoginResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = LoginRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LoginRestControllerTest {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockBean private LoginUser loginUser;

  @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void loginReturnsJwtWhenCredentialsAreValid() throws Exception {
    // given
    var email = "user@email.com";
    var password = "plain-password";
    var loginRequest = new LoginRestController.LoginRequest(email, password);

    when(loginUser.login(new LoginCommand(email, password)))
        .thenReturn(new LoginResult("jwt-token", "Bearer"));

    // when
    mockMvc
        .perform(
            post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("jwt-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"));

    // then
    verify(loginUser).login(new LoginCommand(email, password));
  }
}
