package code.bootstrap;

import code.bootstrap.exceptions.IllegalProfileException;
import java.util.List;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class ValidProfileInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext context) {
    ConfigurableEnvironment env = context.getEnvironment();

    String[] allowed = env.getProperty("spring.allowed-profiles", String[].class);
    if (allowed == null) return;
    List<String> allowedList = List.of(allowed);
    List<String> activeList = List.of(env.getActiveProfiles());
    boolean isValid = activeList.stream().anyMatch(allowedList::contains);
    if (!isValid) {
      throw new IllegalProfileException(activeList, allowedList);
    }
  }
}