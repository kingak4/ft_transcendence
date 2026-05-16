package code;

import code.archgen.StructurizrModuleExporter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
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
      Path basePath = Paths.get("build/tmp/classUtil");
      Files.createDirectories(basePath);

      StructurizrModuleExporter exporter = new StructurizrModuleExporter(modules);
      exporter.export(basePath);

      generateAsciidocIndex(
          modules.stream().sorted(Comparator.comparing(m -> m.getIdentifier().toString())).toList(),
          basePath);

    } catch (Exception e) {
      log.error("Failed to generate diagrams: {}", e.getMessage(), e);
    }
  }

  private void generateAsciidocIndex(List<ApplicationModule> modules, Path basePath)
      throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("== Module Class Diagrams");

    lines.add("");
    lines.add("=== Packages Overview");
    lines.add("plantuml::{classUtil-docs}/packages-overview.puml[format=svg]");

    for (ApplicationModule module : modules) {
      lines.add("");
      lines.add("=== " + module.getDisplayName());

      String prefix = "components-" + sanitizeFileName(module.getIdentifier().toString()) + "-";
      try (var stream = Files.list(basePath)) {
        stream
            .map(p -> p.getFileName().toString())
            .filter(name -> name.startsWith(prefix))
            .sorted()
            .forEach(name -> lines.add("plantuml::{classUtil-docs}/" + name + "[format=svg]"));
      }
    }

    Files.writeString(
        basePath.resolve("all-docs.adoc"), String.join(System.lineSeparator(), lines));
  }

  private static String sanitizeFileName(String rawName) {
    return rawName.replaceAll("[^a-zA-Z0-9\\-_]", "-");
  }
}
