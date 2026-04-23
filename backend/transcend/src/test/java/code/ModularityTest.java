package code;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

  final ApplicationModules modules = ApplicationModules.of(TranscendApp.class);

  @Test
  void verifyModularity() {
    modules.verify();
  }

  @Test
  void createModuleDocumentation() {
    new org.springframework.modulith.docs.Documenter(modules)
        .writeDocumentation()
        .writeIndividualModulesAsPlantUml();
  }
}
