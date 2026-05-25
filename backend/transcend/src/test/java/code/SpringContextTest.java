package code;

import code.bootstrap.DotEnvInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = TranscendApp.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = DotEnvInitializer.class)
class SpringContextTest {

  @Test
  void should_load() {}
}
