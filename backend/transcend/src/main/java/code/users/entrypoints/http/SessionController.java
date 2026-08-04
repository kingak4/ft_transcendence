package code.users.entrypoints.http;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/session")
public class SessionController {

  @GetMapping
  @Operation(summary = "Check if the current session is valid")
  @ApiResponse(responseCode = "200", description = "Session is valid")
  @ApiResponse(responseCode = "401", description = "Session is invalid or expired")
  public ResponseEntity<SessionResponse> checkSession(Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());
    return ResponseEntity.ok(new SessionResponse(userId));
  }

  public record SessionResponse(UUID userId) {}
}
