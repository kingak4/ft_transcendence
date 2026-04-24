package code.users.entrypoints.api;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RegisterController.class)
@Import({UsersExceptionHandler.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class RegisterControllerTest {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockBean private RegisterUseCase registerUseCase;
  @MockBean private UsersApiMapper mapper;
  @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void registerReturns200WithUserIdWhenSuccessful() throws Exception {
    // given
    var email = "john@example.com";
    var password = "password123";
    var request = new RegisterRequest(email, password);
    var command = new RegisterCommand(email, password);
    var uuid = UUID.randomUUID();
    var registeredUser = new RegisteredUser(uuid);
    var response = new RegisterController.RegisterResponse(uuid);

    when(mapper.toCommand(any(RegisterRequest.class))).thenReturn(command);
    when(registerUseCase.register(command)).thenReturn(registeredUser);
    when(mapper.toResponse(registeredUser)).thenReturn(response);

    // when & then
    mockMvc
        .perform(
            post("/users/register")
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
    var email = "john@example.com";
    var password = "password123";
    var request = new RegisterRequest(email, password);
    var command = new RegisterCommand(email, password);

    when(mapper.toCommand(any(RegisterRequest.class))).thenReturn(command);
    when(registerUseCase.register(command)).thenThrow(new EmailAlreadyRegisteredException(email));

    // when & then
    mockMvc
        .perform(
            post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.message").value("Email john@example.com already registered."));

    verify(mapper).toCommand(request);
    verify(registerUseCase).register(command);
  }
}
