package code.users.infrastructure.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(PasswordConstraintValidatorTest.TestConfig.class)
@TestPropertySource(
    properties =
        "spring.security.validation.common-passwords=classpath:validation/common-passwords.txt")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class PasswordConstraintValidatorTest {

  @Configuration
  @Import(PasswordConstraintValidator.class)
  static class TestConfig {}

  private final PasswordConstraintValidator validator;
  private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    validator.initialize(null);
    context = mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);
  }

  @ParameterizedTest
  @ValueSource(strings = {"StrongPass123!", "AnotherValidPassword99"})
  void shouldReturnTrueForValidPasswords(String password) {
    assertTrue(validator.isValid(password, context));
  }

  @ParameterizedTest
  @MethodSource("invalidPasswords")
  void shouldReturnFalseForInvalidPasswords(String password) {
    assertFalse(validator.isValid(password, context));
  }

  static Stream<String> invalidPasswords() {
    return Stream.of(
        null, // Null
        "Short1!", // Too short
        "a".repeat(61), // Too long
        "Pass word123",
        " Password123",
        "Password123\t", // Whitespaces
        "password123",
        "1234567890",
        "qwertyuiop", // Dictionary matches
        "letmein123",
        "welcome123");
  }
}