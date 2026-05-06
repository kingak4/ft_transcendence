package code.users.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

public class DotenvApplicationContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    Dotenv dotenv =
        Dotenv.configure().directory(System.getProperty("user.dir")).ignoreIfMissing().load();

    Map<String, Object> props = new HashMap<>();
    dotenv.entries().forEach(entry -> props.put(entry.getKey(), entry.getValue()));

    applicationContext
        .getEnvironment()
        .getPropertySources()
        .addFirst(new MapPropertySource("dotenvProperties", props));
  }
}
