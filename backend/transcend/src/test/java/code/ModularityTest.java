package code;

import code.archgen.StructurizrModuleExporter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@Slf4j
public class ModularityTest {

  final ApplicationModules modules = ApplicationModules.of(TranscendApp.class);

  @Test
  void verifyModularity() {
    modules.verify();
  }

  @Test
  void createDocumentation() {
    log.info("Generating Diagrams");
    generateModuleDiagram();
    generateClassDiagrams();
  }

  private void generateModuleDiagram() {
    log.info("Generating module diagrams with Modulith");
    var options = Documenter.Options.defaults().withOutputFolder("build/tmp/modulith");
    new Documenter(modules, options).writeDocumentation().writeIndividualModulesAsPlantUml();
  }

  private void generateClassDiagrams() {
    log.info("Generating diagrams via Structurizr");
    try {
      Path basePath = Paths.get("build/tmp/structurizr");
      Files.createDirectories(basePath);

      StructurizrModuleExporter exporter = new StructurizrModuleExporter(modules);
      exporter.export(basePath);

    } catch (Exception e) {
      log.error("Failed to generate diagrams: {}", e.getMessage(), e);
    }
  }
}
