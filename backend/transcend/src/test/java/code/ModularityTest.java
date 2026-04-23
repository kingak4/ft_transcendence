package code;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

  @Test
  void verifyModularity() {
    ApplicationModules.of(TranscendApp.class).verify();
  }
}
