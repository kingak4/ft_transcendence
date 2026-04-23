package code.users.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import code.users.config.EncoderConfig;
import code.users.ports.out.HashingService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(BCryptHashingServiceTest.BCryptHashingServiceTestConfig.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class BCryptHashingServiceTest {

  private final HashingService hashingService;
  private final PasswordEncoder passwordEncoder;

  @Configuration
  @Import(BCryptHashingService.class)
  static class BCryptHashingServiceTestConfig extends EncoderConfig {}

  @Test
  void encodeProducesPasswordThatMatchesRawInput() {
    // given
    var rawPassword = "plain-password";

    // when
    var encoded = hashingService.encode(rawPassword);
    System.out.println(encoded);

    // then
    assertNotEquals(rawPassword, encoded);
    assertTrue(passwordEncoder.matches(rawPassword, encoded));
  }
}
