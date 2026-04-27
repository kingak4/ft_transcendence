package code.users.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.passay.DictionaryRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Slf4j
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

  @Value("${app.security.validation.common-passwords}")
  private Resource dictionaryResource;

  private PasswordValidator validator;

  @Override
  public void initialize(ValidPassword constraintAnnotation) {
    List<Rule> rules = new ArrayList<>();
    rules.add(new LengthRule(10, 60));
    rules.add(new WhitespaceRule());

    if (dictionaryResource != null && dictionaryResource.exists()) {
      try (Reader reader = new InputStreamReader(dictionaryResource.getInputStream())) {
        WordListDictionary dictionary =
            new WordListDictionary(
                WordLists.createFromReader(new Reader[] {reader}, false, new ArraysSort()));
        rules.add(new DictionaryRule(dictionary));
      } catch (Exception e) {
        log.error("Failed to load common password dictionary.", e);
      }
    } else {
      log.warn("Password dictionary resource not found at configured path.");
    }
    this.validator = new PasswordValidator(rules);
  }

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    return Optional.ofNullable(password)
        .map(PasswordData::new)
        .map(validator::validate)
        .map(result -> evaluateResult(result, context))
        .orElse(false);
  }

  private boolean evaluateResult(RuleResult result, ConstraintValidatorContext context) {
    if (result.isValid()) return true;

    context.disableDefaultConstraintViolation();

    validator
        .getMessages(result)
        .forEach(
            message ->
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation());
    return false;
  }
}
