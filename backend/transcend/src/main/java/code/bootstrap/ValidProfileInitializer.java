package code.bootstrap;

import code.bootstrap.exceptions.IllegalProfileException;
import java.util.List;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class ValidProfileInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext context) {

    ConfigurableEnvironment env = context.getEnvironment();

    String[] allowed =
        Binder.get(env).bind("spring.allowed-profiles", String[].class).orElse(new String[0]);
    List<String> allowedList = List.of(allowed);
    List<String> activeList = List.of(env.getActiveProfiles());

    if (allowedList.isEmpty()) return;

    boolean isValid = activeList.stream().anyMatch(allowedList::contains);
    if (!isValid) {
      throw new IllegalProfileException(activeList, allowedList);
    }
  }
}
