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
import java.util.stream.Collectors;
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
    log.info("Generating module diagrams");
    var options = Documenter.Options.defaults().withOutputFolder("build/tmp/modulith");
    new Documenter(modules, options).writeDocumentation().writeIndividualModulesAsPlantUml();
  }

  private void generateClassDiagrams() {
    log.info("Generating class diagrams");
    try {
      Path elnarionPath = Paths.get("build/tmp/elnarion");
      Files.createDirectories(elnarionPath);

      var sortedModules =
          modules.stream()
              .sorted(Comparator.comparing(module -> module.getIdentifier().toString()))
              .toList();
      var modulePackages =
          sortedModules.stream().map(module -> module.getBasePackage().getName()).toList();

      writeDiagram(elnarionPath.resolve("components.puml"), modulePackages);

      var docs = new ArrayList<String>();
      docs.add("== Module Class Diagrams");

      for (var module : sortedModules) {
        var moduleName = module.getIdentifier().toString();
        var fileName = "components-" + sanitizeFileName(moduleName) + ".puml";
        writeDiagram(elnarionPath.resolve(fileName), List.of(module.getBasePackage().getName()));

        docs.add("");
        docs.add("=== " + module.getDisplayName());
        docs.add("plantuml::{elnarion-docs}/" + fileName + "[format=svg]");
      }

      Files.writeString(
          elnarionPath.resolve("all-docs.adoc"),
          String.join(System.lineSeparator(), docs) + System.lineSeparator());
    } catch (IOException e) {
      e.printStackTrace();
    }
    {
    }
  }

  private static void writeDiagram(Path outputFile, List<String> scanPackages) throws IOException {
    var config =
        new PlantUMLClassDiagramConfigBuilder(scanPackages)
            .withJPAAnnotations(true)
            .withValidationAnnotations(true)
            .withUseSmetana(false)
            .withUseShortClassNames(true)
            .withUseShortClassNamesInFieldsAndMethods(true)
            .build();
    var diagram = new PlantUMLClassDiagramGenerator(config).generateDiagramText();
    if (!diagram.contains("left to right direction")) {
      diagram =
          diagram.replace(
              "@startuml", "@startuml" + System.lineSeparator() + "left to right direction");
    }
    Files.writeString(outputFile, diagram);
  }

  private static String sanitizeFileName(String rawName) {
    return rawName
        .chars()
        .mapToObj(
            character -> {
              if (Character.isLetterOrDigit(character) || character == '-' || character == '_') {
                return String.valueOf((char) character);
              }
              return "-";
            })
        .collect(Collectors.joining(""));
  }
}
