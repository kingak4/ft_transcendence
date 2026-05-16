package code.archgen;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class DiagramRenderer {

  private final GraphLayoutStrategy layoutStrategy =
      new GraphLayoutStrategy.HybridDecoupledLayout();
  private final PlantUMLBuilder plantUmlBuilder = new PlantUMLBuilder(layoutStrategy);

  public void renderAndWrite(JavaClasses classes, Path outputPath) throws IOException {
    var relationships = buildRelationships(classes);
    String diagramText = plantUmlBuilder.buildDiagram(classes, relationships);
    Files.writeString(outputPath, diagramText);
  }

  public Set<String> buildRelationships(JavaClasses classes) {
    Set<String> relations = new HashSet<>();
    for (JavaClass clazz : classes) {
      String sourceAlias = RenderUtils.getAlias(clazz);
      for (Dependency dep : clazz.getDirectDependenciesFromSelf()) {
        JavaClass target = dep.getTargetClass();
        if (classes.contain(target.getName()) && !clazz.equals(target)) {
          String targetAlias = RenderUtils.getAlias(target);
          relations.add(String.format("%s --> %s", sourceAlias, targetAlias));
        }
      }
    }

    return relations;
  }
}
