package code.archgen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

/** Generates an Asciidoc index referencing generated PlantUML diagrams. */
class AsciidocGenerator {

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

    lines.add("");
    lines.add("=== Packages Overview");
    lines.add("plantuml::{structurizr-docs}/packages-overview.puml[format=svg]");

    for (ApplicationModule module : list) {
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
