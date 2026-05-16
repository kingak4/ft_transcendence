package code.archgen;

import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.view.ComponentView;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.SneakyThrows;

class ViewWriter {
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
}
