package code.users.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

// openssl rand -base64 32
@TestPropertySource(
    properties = {
      "security.jwt.secret=pXbFiUSy8W76pDARDCfcWzpSymurWCST1jYh46oBwrA=",
      "security.jwt.expirationMs=3600000"
    })
@SpringJUnitConfig(JwtTokenServiceTest.JwtTokenServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class JwtTokenServiceTest {

  private final JwtTokenService jwtTokenService;
  private final JwtProperties jwtProperties;

  private UserDetails testUser;

  @Configuration
  @EnableConfigurationProperties(JwtProperties.class)
  @Import({JwtTokenService.class})
  static class JwtTokenServiceTestConfig {}

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "password", Collections.emptyList());
  }

  @Test
  void generateToken_ReturnsValidToken() {
    // given
    String username = testUser.getUsername();

    // when
    String token = jwtTokenService.generateToken(username);

    // then
    assertNotNull(token);
    assertFalse(token.isEmpty());
    assertEquals(username, jwtTokenService.extractUsername(token));
  }

  @Test
  void isTokenValid_WithValidToken_ReturnsTrue() {
    // given
    String token = jwtTokenService.generateToken(testUser.getUsername());

    // when
    boolean isValid = jwtTokenService.isTokenValid(token, testUser);

    // then
    assertTrue(isValid);
  }

  @Test
  void isTokenValid_WithDifferentUser_ReturnsFalse() {
    // given
    String token = jwtTokenService.generateToken("other@example.com");

    // when
    boolean isValid = jwtTokenService.isTokenValid(token, testUser);

    // then
    assertFalse(isValid);
  }

  @Test
  void isTokenValid_WithExpiredToken_ReturnsFalse() throws InterruptedException {
    // given
    JwtProperties props = new JwtProperties(jwtProperties.getSecret(), 1);
    JwtTokenService shortLivedService = new JwtTokenService(props);

    // when
    String token = shortLivedService.generateToken(testUser.getUsername());
    Thread.sleep(10);

    // then
    assertThrows(
        io.jsonwebtoken.ExpiredJwtException.class,
        () -> shortLivedService.isTokenValid(token, testUser));
  }
}
