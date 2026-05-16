package code.archgen;

import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.view.ComponentView;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.modulith.core.ApplicationModule;

class DiagramRenderer {
  private final StructurizrPlantUMLExporter exporter = new StructurizrPlantUMLExporter();

  @SneakyThrows
  public void writeView(ComponentView view, Path file) {
    String puml = exporter.export(view).getDefinition();

    puml = puml.replace("left to right direction", "");

    String layoutSettings =
        """
            @startuml
            left to right direction
            skinparam ranksep 100
            skinparam nodesep 40
            skinparam linetype ortho
            skinparam autoproxy true
            """;

    puml = puml.replace("@startuml", layoutSettings);

    Files.writeString(file, puml);
  }

  @SneakyThrows
  public void generateAsciidocIndex(List<ApplicationModule> modules, Path basePath) {
    List<String> lines = new ArrayList<>();
    lines.add("== Module Class Diagrams");

    lines.add("");
    lines.add("=== Packages Overview");
    lines.add("plantuml::{structurizr-docs}/packages-overview.puml[format=svg]");

    for (ApplicationModule module : modules) {
      lines.add("");
      lines.add("=== " + module.getDisplayName());

      String prefix =
          "components-" + DiagramUtils.sanitize(module.getIdentifier().toString()) + "-";
      try (var stream = Files.list(basePath)) {
        stream
            .map(p -> p.getFileName().toString())
            .filter(name -> name.startsWith(prefix))
            .sorted()
            .forEach(name -> lines.add("plantuml::{structurizr-docs}/" + name + "[format=svg]"));
      }
    }

    Files.writeString(
        basePath.resolve("all-docs.adoc"), String.join(System.lineSeparator(), lines));
  }
}
