package code;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

  final ApplicationModules modules = ApplicationModules.of(TranscendApp.class);

  @Test
  void verifyModularity() {
    modules.verify();
  }

  @Test
  void createModuleDocumentation() {
    new org.springframework.modulith.docs.Documenter(modules)
        .writeDocumentation()
        .writeIndividualModulesAsPlantUml();
    System.out.println("START GENEROWANIA DIAGRAMÓW");
    generateFileTreeDiagrams();
    generateClassDiagrams();
  }

  @SneakyThrows
  private void generateClassDiagrams() {
    JavaClasses classes = new ClassFileImporter().importPackages("code");
    System.out.println("Liczba klas: " + classes.size());
  }

  private void generateFileTreeDiagrams() {}

//  private void generateClassDiagram(JavaClasses classes) throws Exception {


//
//    StringBuilder plantUml = new StringBuilder();
//    plantUml.append("@startuml\n");
//
//    classes.stream()
//        .map(c -> c.getName())
//        .filter(
//            name ->
//                !name.contains("Test")
//                    && !name.contains("$")
//                    && !name.startsWith("java.")
//                    && !name.startsWith("javax.")
//                    && !name.startsWith("jakarta.")
//                    && !name.startsWith("lombok.")
//                    && !name.startsWith("org.springframework."))
//        .forEach(name -> plantUml.append("class ").append(name).append("\n"));
//
//    classes.forEach(
//        c -> {
//          c.getDirectDependenciesFromSelf()
//              .forEach(
//                  dep -> {
//                    String from = c.getName();
//                    String to = dep.getTargetClass().getName();
//
//                    if (to.startsWith("java.")
//                        || to.startsWith("javax.")
//                        || to.startsWith("jakarta.")
//                        || to.startsWith("lombok.")
//                        || to.startsWith("org.springframework.")) {
//                      return;
//                    }
//
//                    plantUml.append(from).append(" --> ").append(to).append("\n");
//                  });
//        });
//
//    plantUml.append("@enduml\n");
//
//    Path path = Paths.get("build/class-diagram.puml");
//    Files.writeString(path, plantUml.toString());
//
//    System.out.println("Diagram zapisany: " + path.toAbsolutePath());
//  }
}
