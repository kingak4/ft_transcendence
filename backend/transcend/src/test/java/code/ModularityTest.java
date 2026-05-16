package code;

import de.elnarion.util.plantuml.generator.classdiagram.PlantUMLClassDiagramGenerator;
import de.elnarion.util.plantuml.generator.classdiagram.config.PlantUMLClassDiagramConfigBuilder;
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
    log.info("Generating module diagrams");
    var options = Documenter.Options.defaults().withOutputFolder("build/tmp/modulith");
    new Documenter(modules, options).writeDocumentation().writeIndividualModulesAsPlantUml();
  }

  private void generateClassDiagrams() {
    log.info("Generating class diagrams");
    try {
      Path basePath = Paths.get("build/tmp/elnarion");
      Files.createDirectories(basePath);

      List<ApplicationModule> modules =
          this.modules.stream()
              .sorted(Comparator.comparing(module -> module.getIdentifier().toString()))
              .toList();

      generateComponentsDiagram(modules, basePath);
      generateModuleDiagrams(modules, basePath);
      generateAsciidocIndex(modules, basePath);

    } catch (IOException e) {
      log.error("Failed to generate diagrams: {}", e.getMessage());
    }
  }

  private void generateModuleDiagrams(List<ApplicationModule> modules, Path basePath)
      throws IOException {
    for (ApplicationModule module : modules) {
      String fileName = getDiagramFileName(module);
      String diagramText = generatePlantUmlDiagram(List.of(module.getBasePackage().getName()));
      Files.writeString(basePath.resolve(fileName), diagramText);
    }
  }

  private void generateComponentsDiagram(List<ApplicationModule> modules, Path basePath)
      throws IOException {
    List<String> allPackages = modules.stream().map(m -> m.getBasePackage().getName()).toList();
    Files.writeString(basePath.resolve("components.puml"), generatePlantUmlDiagram(allPackages));
  }

  private String generatePlantUmlDiagram(List<String> scanPackages) {
    var config =
        new PlantUMLClassDiagramConfigBuilder(scanPackages)
            .withJPAAnnotations(true)
            .withValidationAnnotations(true)
            .withUseSmetana(false)
            .withUseShortClassNames(true)
            .withUseShortClassNamesInFieldsAndMethods(true)
            .build();

    return new PlantUMLClassDiagramGenerator(config).generateDiagramText();
  }

  private void generateAsciidocIndex(List<ApplicationModule> modules, Path basePath)
      throws IOException {
    var docs = new ArrayList<String>();
    docs.add("== Module Class Diagrams");

    for (ApplicationModule module : modules) {
      docs.add("");
      docs.add("=== " + module.getDisplayName());
      docs.add("plantuml::{elnarion-docs}/" + getDiagramFileName(module) + "[format=svg]");
    }

    String asciidocContent = String.join(System.lineSeparator(), docs) + System.lineSeparator();
    Files.writeString(basePath.resolve("all-docs.adoc"), asciidocContent);
  }

  private String getDiagramFileName(ApplicationModule module) {
    String moduleName = module.getIdentifier().toString();
    return "components-" + sanitizeFileName(moduleName) + ".puml";
  }

  private static String sanitizeFileName(String rawName) {
    return rawName.replaceAll("[^a-zA-Z0-9\\-_]", "-");
  }
}
