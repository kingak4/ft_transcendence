package code;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    log.info("Generating class diagrams via ArchUnit");
    try {
      Path basePath = Paths.get("build/tmp/classUtil");
      Files.createDirectories(basePath);

      List<ApplicationModule> sortedModules =
          modules.stream()
              .sorted(Comparator.comparing(module -> module.getIdentifier().toString()))
              .toList();

      generateComponentsDiagram(sortedModules, basePath);
      for (ApplicationModule module : sortedModules) {
        generateModuleDiagram(module, basePath);
      }
      generateAsciidocIndex(sortedModules, basePath);

    } catch (IOException e) {
      log.error("Failed to generate diagrams: {}", e.getMessage());
    }
  }

  private void generateComponentsDiagram(List<ApplicationModule> modules, Path basePath)
      throws IOException {
    String[] packages =
        modules.stream().map(m -> m.getBasePackage().getName()).toArray(String[]::new);

    JavaClasses allContextClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(packages);

    String diagramText = renderPlantUml(allContextClasses);
    Files.writeString(basePath.resolve("components.puml"), diagramText);
  }

  private void generateModuleDiagram(ApplicationModule module, Path basePath) throws IOException {
    JavaClasses moduleClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(module.getBasePackage().getName());
    String diagramText = renderPlantUml(moduleClasses);
    String fileName = getDiagramFileName(module);
    Files.writeString(basePath.resolve(fileName), diagramText);
  }

  private String renderPlantUml(JavaClasses classes) {
    StringBuilder sb = new StringBuilder();
    sb.append("@startuml").append(System.lineSeparator());
    sb.append("left to right direction").append(System.lineSeparator());
    sb.append("hide methods").append(System.lineSeparator());
    sb.append("hide fields").append(System.lineSeparator());
    sb.append("skinparam packageStyle folder").append(System.lineSeparator());

    // 1. Group Classes by Package for visual grouping
    Map<String, List<JavaClass>> grouped =
        classes.stream().collect(Collectors.groupingBy(JavaClass::getPackageName));

    for (Map.Entry<String, List<JavaClass>> entry : grouped.entrySet()) {
      sb.append("package \"").append(entry.getKey()).append("\" {").append(System.lineSeparator());
      for (JavaClass clazz : entry.getValue()) {
        String type = clazz.isInterface() ? "interface" : (clazz.isEnum() ? "enum" : "class");
        // Use unique alias (replace dots with underscores) to avoid PlantUML parsing errors
        String alias = clazz.getName().replace(".", "_");
        sb.append(String.format("  %s \"%s\" as %s", type, clazz.getSimpleName(), alias))
            .append(System.lineSeparator());
      }
      sb.append("}").append(System.lineSeparator());
    }

    Set<String> relations = new HashSet<>();
    for (JavaClass clazz : classes) {
      String sourceAlias = clazz.getName().replace(".", "_");
      for (Dependency dep : clazz.getDirectDependenciesFromSelf()) {
        JavaClass target = dep.getTargetClass();
        if (classes.contain(target.getName()) && !clazz.equals(target)) {
          String targetAlias = target.getName().replace(".", "_");
          relations.add(String.format("%s --> %s", sourceAlias, targetAlias));
        }
      }
    }

    relations.forEach(r -> sb.append(r).append(System.lineSeparator()));
    sb.append("@enduml");
    return sb.toString();
  }

  private void generateAsciidocIndex(List<ApplicationModule> modules, Path basePath)
      throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("== Module Class Diagrams");

    for (ApplicationModule module : modules) {
      lines.add("");
      lines.add("=== " + module.getDisplayName());
      lines.add("plantuml::{classUtil-docs}/" + getDiagramFileName(module) + "[format=svg]");
    }

    Files.writeString(
        basePath.resolve("all-docs.adoc"), String.join(System.lineSeparator(), lines));
  }

  private String getDiagramFileName(ApplicationModule module) {
    return "components-" + sanitizeFileName(module.getIdentifier().toString()) + ".puml";
  }

  private static String sanitizeFileName(String rawName) {
    return rawName.replaceAll("[^a-zA-Z0-9\\-_]", "-");
  }
}
