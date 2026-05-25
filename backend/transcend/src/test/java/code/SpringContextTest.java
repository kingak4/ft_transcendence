package code;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import code.bootstrap.DotEnvInitializer;

@SpringBootTest(classes = TranscendApp.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = DotEnvInitializer.class)
class SpringContextTest {

  @Test
  void should_load() {}
}
