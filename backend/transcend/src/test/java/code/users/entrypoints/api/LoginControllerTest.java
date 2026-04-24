package code.users.entrypoints.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.infrastructure.security.JwtAuthenticationFilter;
import code.users.ports.in.LoginUseCase;
import code.users.ports.in.LoginUseCase.LoginCommand;
import code.users.ports.in.LoginUseCase.LoginResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class LoginControllerTest {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockitoBean private LoginUseCase loginUseCase;
  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
  @MockitoBean private UsersApiMapper loginMapper;

  @Test
  void loginReturnsJwtWhenCredentialsAreValid() throws Exception {
    // given
    var email = "user@email.com";
    var password = "plain-password";
    var loginRequest = new LoginController.LoginRequest(email, password);
    var loginCommand = new LoginCommand(email, password);
    var loginResult = new LoginResult("jwt-token", "Bearer");
    var loginResponse = new LoginController.LoginResponse("jwt-token", "Bearer");

    when(loginMapper.toCommand(loginRequest)).thenReturn(loginCommand);
    when(loginUseCase.login(loginCommand)).thenReturn(loginResult);
    when(loginMapper.toResponse(loginResult)).thenReturn(loginResponse);

    // when
    mockMvc
        .perform(
            post("/" + LoginController.BASE_URL + "/" + LoginController.LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("jwt-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"));

    // then
    verify(loginMapper).toCommand(loginRequest);
    verify(loginUseCase).login(loginCommand);
    verify(loginMapper).toResponse(loginResult);
  }
}
