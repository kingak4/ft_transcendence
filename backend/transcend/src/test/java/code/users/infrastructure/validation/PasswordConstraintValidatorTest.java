package code.users.infrastructure.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

class PasswordConstraintValidatorTest {

  private PasswordConstraintValidator validator;
  private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    validator = new PasswordConstraintValidator();
    String dict = "1234567890\nletmein123\npassword123\nqwertyuiop\niloveyou123\n";
    ReflectionTestUtils.setField(
        validator, "dictionaryResource", new ByteArrayResource(dict.getBytes()));
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
        "iloveyou123");
  }
}
