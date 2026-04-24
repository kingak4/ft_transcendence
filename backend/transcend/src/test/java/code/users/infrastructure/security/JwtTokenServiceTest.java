package code.users.infrastructure.security;

import static code.users.domain.model.UserFixtures.EMAIL_FIXTURE;
import static code.users.domain.model.UserFixtures.PASSWORD_FIXTURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
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
  @Configuration
  @EnableConfigurationProperties(JwtProperties.class)
  @Import({JwtTokenService.class})
  static class JwtTokenServiceTestConfig {}

  private final JwtTokenService jwtTokenService;
  private final JwtProperties jwtProperties;

  @Test
  void generateToken_ReturnsValidToken() {
    // given
    User testUser = new User(EMAIL_FIXTURE, PASSWORD_FIXTURE, Collections.emptyList());
    ;

    // when
    String token = jwtTokenService.generateToken(EMAIL_FIXTURE);
    boolean isValid = jwtTokenService.isTokenValid(token, testUser);

    // then
    assertNotNull(token);
    assertFalse(token.isEmpty());
    assertEquals(EMAIL_FIXTURE, jwtTokenService.extractUsername(token));
    assertTrue(isValid);
  }

  @Test
  void isTokenValid_WithDifferentUser_ReturnsFalse() {
    // given
    User testUser = new User(EMAIL_FIXTURE, PASSWORD_FIXTURE, Collections.emptyList());
    ;
    String otherEmail = "other@example.com";
    String token = jwtTokenService.generateToken(otherEmail);

    // when
    boolean isValid = jwtTokenService.isTokenValid(token, testUser);

    // then
    assertFalse(isValid);
  }

  @Test
  void isTokenValid_WithExpiredToken_ReturnsFalse() throws InterruptedException {
    // given
    User testUser = new User(EMAIL_FIXTURE, PASSWORD_FIXTURE, Collections.emptyList());
    ;
    JwtProperties props = new JwtProperties(jwtProperties.getSecret(), 1);
    JwtTokenService shortLivedService = new JwtTokenService(props);

    // when
    String token = shortLivedService.generateToken(EMAIL_FIXTURE);
    Thread.sleep(10);

    // then
    assertThrows(
        io.jsonwebtoken.ExpiredJwtException.class,
        () -> shortLivedService.isTokenValid(token, testUser));
  }
}
