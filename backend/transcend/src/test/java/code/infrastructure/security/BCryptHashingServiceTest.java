package code.infrastructure.security;

import code.bootstrap.config.EncoderConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig
@ContextConfiguration(classes = BCryptHashingServiceTest.BCryptHashingServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BCryptHashingServiceTest {

  private final BCryptHashingService hashingService;
  private final PasswordEncoder passwordEncoder;

  @Configuration
  @Import(BCryptHashingService.class)
  static class BCryptHashingServiceTestConfig extends EncoderConfig {
  }

  @Test
  void encodeProducesPasswordThatMatchesRawInput() {
    // given
    var rawPassword = "plain-password";

    // when
    var encoded = hashingService.encode(rawPassword);

    // then
    assertNotEquals(rawPassword, encoded);
    assertTrue(passwordEncoder.matches(rawPassword, encoded));
  }
}