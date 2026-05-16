package code.archgen;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlantUMLBuilder {

  private final GraphLayoutStrategy layoutStrategy;

  public Map<String, List<JavaClass>> groupByPackage(JavaClasses classes) {
    return classes.stream().collect(Collectors.groupingBy(JavaClass::getPackageName));
  }

  public String buildDiagram(JavaClasses classes, Set<String> relationships) {
    StringBuilder sb = new StringBuilder();

    sb.append("@startuml").append(System.lineSeparator());
    sb.append(layoutStrategy.getLayoutDirectives()).append(System.lineSeparator());
    sb.append("hide methods").append(System.lineSeparator());
    sb.append("hide fields").append(System.lineSeparator());
    sb.append("skinparam packageStyle folder").append(System.lineSeparator());
    sb.append(layoutStrategy.getSkinparamSettings()).append(System.lineSeparator());

    appendClassDeclarations(sb, classes);
    appendRelationships(sb, relationships);
    sb.append("@enduml");

    return sb.toString();
  }

  private void appendClassDeclarations(StringBuilder sb, JavaClasses classes) {
    Map<String, List<JavaClass>> grouped = groupByPackage(classes);
    for (Map.Entry<String, List<JavaClass>> entry : grouped.entrySet()) {
      sb.append("package \"").append(entry.getKey()).append("\" {").append(System.lineSeparator());
      for (JavaClass clazz : entry.getValue()) {
        appendClassDeclaration(sb, clazz);
      }
      sb.append("}").append(System.lineSeparator());
    }
  }

  private void appendClassDeclaration(StringBuilder sb, JavaClass clazz) {
    String type = determineClassType(clazz);
    String alias = RenderUtils.getAlias(clazz);
    String simpleName = RenderUtils.getSimpleName(clazz);

    sb.append(String.format("  %s \"%s\" as %s", type, simpleName, alias))
        .append(System.lineSeparator());
  }

  private String determineClassType(JavaClass clazz) {
    if (clazz.isInterface()) {
      return "interface";
    } else if (clazz.isEnum()) {
      return "enum";
    } else {
      return "class";
    }
  }

  private void appendRelationships(StringBuilder sb, Set<String> relationships) {
    relationships.forEach(r -> sb.append(r).append(System.lineSeparator()));
  }
}
