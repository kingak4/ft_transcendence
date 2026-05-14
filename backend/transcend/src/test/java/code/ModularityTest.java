package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@Slf4j
class ModularityTest {

  final ApplicationModules modules = ApplicationModules.of(TranscendApp.class);

  @Test
  void verifyModularity() {
    modules.verify();
  }

  @Test
  void createDocumentation() {
    log.info("Generating Diagrams");
    generateModuleDiagram();
    generateFileTreeDiagrams();
    generateClassDiagrams();
  }

  private void generateModuleDiagram() {
    var options = Documenter.Options.defaults().withOutputFolder("build/tmp/modulith");
    new Documenter(modules, options).writeDocumentation().writeIndividualModulesAsPlantUml();
  }

  private void generateClassDiagrams() {}

  private void generateFileTreeDiagrams() {}
}
