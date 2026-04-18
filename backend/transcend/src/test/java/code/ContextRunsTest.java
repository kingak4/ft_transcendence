package code;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TranscendApp.class)
@ActiveProfiles("test")
class ContextRunsTest {

  @Test
  void should_load() {}
}
