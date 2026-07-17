package code.bootstrap;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.Profiles;

@Slf4j
public class DotEnvInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    String userDir = System.getProperty("user.dir");

    Map<String, Object> props = new HashMap<>();
    log.info("Loading .env properties");
    loadEnv(userDir, props);

    boolean isLocalProfileActive =
        applicationContext.getEnvironment().acceptsProfiles(Profiles.of("local"));

    if (isLocalProfileActive) {
      log.info("Loading .env.local overrides");
      overrideWithLocalEnv(userDir, props);
    }

    if (!props.isEmpty()) {
      applicationContext
          .getEnvironment()
          .getPropertySources()
          .addFirst(new MapPropertySource("dotenvProperties", props));
    }
  }

  private static void loadEnv(String userDir, Map<String, Object> props) {
    Dotenv dotenv = Dotenv.configure().directory(userDir).filename(".env").ignoreIfMissing().load();

    dotenv.entries().forEach(entry -> props.put(entry.getKey(), entry.getValue()));
  }

  private void overrideWithLocalEnv(String userDir, Map<String, Object> props) {
    Dotenv dotenvLocal =
        Dotenv.configure().directory(userDir).filename(".env.local").ignoreIfMissing().load();
    dotenvLocal.entries().forEach(entry -> props.put(entry.getKey(), entry.getValue()));
  }
}
