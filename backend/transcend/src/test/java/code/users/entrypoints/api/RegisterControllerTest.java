package code.users.entrypoints.api;

import static code.users.domain.model.UserFixtures.EMAIL_FIXTURE;
import static code.users.domain.model.UserFixtures.PASSWORD_FIXTURE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.shared.exceptions.GlobalExceptionHandler;
import code.users.domain.exceptions.EmailAlreadyRegisteredException;
import code.users.entrypoints.api.RegisterController.RegisterRequest;
import code.users.entrypoints.api.mappers.UsersApiMapper;
import code.users.infrastructure.security.JwtAuthenticationFilter;
import code.users.ports.in.RegisterUseCase;
import code.users.ports.in.RegisterUseCase.RegisterCommand;
import code.users.ports.in.RegisterUseCase.RegisteredUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RegisterController.class)
@Import({UsersExceptionHandler.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class RegisterControllerTest {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockitoBean private RegisterUseCase registerUseCase;
  @MockitoBean private UsersApiMapper mapper;
  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void registerReturns200WithUserIdWhenSuccessful() throws Exception {
    // given
    var request = new RegisterRequest(EMAIL_FIXTURE, PASSWORD_FIXTURE);
    var command = new RegisterCommand(EMAIL_FIXTURE, PASSWORD_FIXTURE);
    var uuid = UUID.randomUUID();
    var registeredUser = new RegisteredUser(code.users.domain.model.UserId.of(uuid));
    var response = new RegisterController.RegisterResponse(uuid);

    when(mapper.toCommand(any(RegisterRequest.class))).thenReturn(command);
    when(registerUseCase.register(command)).thenReturn(registeredUser);
    when(mapper.toResponse(registeredUser)).thenReturn(response);

    // when & then
    mockMvc
        .perform(
            post("/" + RegisterController.BASE_URL + "/" + RegisterController.REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(uuid.toString()));

    verify(mapper).toCommand(request);
    verify(registerUseCase).register(command);
    verify(mapper).toResponse(registeredUser);
  }

  @Test
  void registerReturns409WhenEmailAlreadyExists() throws Exception {
    // given
    var request = new RegisterRequest(EMAIL_FIXTURE, PASSWORD_FIXTURE);
    var command = new RegisterCommand(EMAIL_FIXTURE, PASSWORD_FIXTURE);

    when(mapper.toCommand(any(RegisterRequest.class))).thenReturn(command);
    when(registerUseCase.register(command))
        .thenThrow(new EmailAlreadyRegisteredException(EMAIL_FIXTURE));

    // when & then
    mockMvc
        .perform(
            post("/" + RegisterController.BASE_URL + "/" + RegisterController.REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(
            jsonPath("$.message")
                .value(String.format(EmailAlreadyRegisteredException.MESSAGE, EMAIL_FIXTURE)));

    verify(mapper).toCommand(request);
    verify(registerUseCase).register(command);
  }
}
