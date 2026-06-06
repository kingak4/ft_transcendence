package code;

import code.bootstrap.DotEnvInitializer;
import code.shared.config.EmbeddedRedisTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = TranscendApp.class)
@ActiveProfiles("test")
@ContextConfiguration(initializers = DotEnvInitializer.class)
class SpringContextTest extends EmbeddedRedisTestSupport {

  @Test
  @WithMockUser
  void should_load() {}
}
