package code.archgen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

/** Generates an Asciidoc index referencing generated PlantUML diagrams. */
class AsciidocGenerator {
  private final Map<String, List<DiagramEntry>> diagramEntriesByModule = new LinkedHashMap<>();

  private record DiagramEntry(String packageShortName, String pumlFileName) {}

  @SneakyThrows
  public void generateIndex(ApplicationModules modules, Path basePath) {
    generateIndex((Iterable<ApplicationModule>) modules, basePath);
  }

  @SneakyThrows
  public void generateIndex(Iterable<ApplicationModule> modules, Path basePath) {
    List<ApplicationModule> list = new ArrayList<>();
    for (ApplicationModule m : modules) list.add(m);

    list.sort(Comparator.comparing(m -> m.getIdentifier().toString()));

    List<String> lines = new ArrayList<>();
    lines.add("== Module Class Diagrams");

    for (ApplicationModule module : list) {
      lines.add("");
      lines.add("=== " + module.getDisplayName());

      List<DiagramEntry> entries =
          new ArrayList<>(diagramEntriesByModule.getOrDefault(module.getDisplayName(), List.of()));
      entries.sort(Comparator.comparing(DiagramEntry::packageShortName));

      for (DiagramEntry entry : entries) {
        lines.add("");
        lines.add("==== " + entry.packageShortName());
        lines.add("plantuml::{structurizr-docs}/" + entry.pumlFileName() + "[format=svg]");
      }
    }

    java.nio.file.Files.writeString(
        basePath.resolve("all-docs.adoc"), String.join(System.lineSeparator(), lines));
  }

  public void addDiagramEntry(String moduleName, String packageShortName, String pumlFileName) {
    diagramEntriesByModule
        .computeIfAbsent(moduleName, key -> new ArrayList<>())
        .add(new DiagramEntry(packageShortName, pumlFileName));
  }

  public void clearDiagramEntries() {
    diagramEntriesByModule.clear();
  }
}
