package code;

import code.structurizr.StructurizrWorkspaceGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class StructurizrGenerator {

  public static void main(String[] args) {
    try {
      String basePackage = args.length > 0 ? args[0] : "code";
      File file = new File("docker/structurizr/data/workspace.json");

      StructurizrWorkspaceGenerator generator = new StructurizrWorkspaceGenerator();
      generator.generate(basePackage, file);
      log.info("Workspace exported to: {}", file.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}