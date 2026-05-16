package code.archgen;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;

class PackageOverviewBuilder {

  public static String build(
      Set<String> modulePackages,
      JavaClasses allClasses,
      Set<String> topLevelPackages,
      Set<String> relations) {

    StringBuilder sb = new StringBuilder();
    sb.append("@startuml").append(System.lineSeparator());
    sb.append("hide methods").append(System.lineSeparator());
    sb.append("hide fields").append(System.lineSeparator());
    sb.append("skinparam packageStyle folder").append(System.lineSeparator());

    for (String modulePkg : modulePackages) {
      String moduleAlias = RenderUtils.sanitizeFileName(modulePkg);

      // 1. Identify "Open" classes (directly in the module root)
      Set<JavaClass> openClasses =
          allClasses.stream()
              .filter(c -> c.getPackageName().equals(modulePkg))
              .collect(Collectors.toSet());

      // 2. Identify "Closed" classes that are direct dependencies of "Open" classes
      Set<JavaClass> referencedClosedClasses = getJavaClasses(modulePkg, openClasses);

      // 3. Render the Module Package Container
      sb.append("package \"")
          .append(modulePkg)
          .append("\" as ")
          .append(moduleAlias)
          .append(" {")
          .append(System.lineSeparator());

      // Render Open Classes
      for (JavaClass clazz : openClasses) {
        renderClassNode(sb, clazz);
      }

      // Render Referenced Closed Classes (The Exception)
      for (JavaClass clazz : referencedClosedClasses) {
        renderClassNode(sb, clazz, "<<internal>>");
      }

      // 4. Render Internal Dependencies (Open -> Closed)
      for (JavaClass openClass : openClasses) {
        for (Dependency dep : openClass.getDirectDependenciesFromSelf()) {
          JavaClass target = dep.getTargetClass();
          if (referencedClosedClasses.contains(target)) {
            sb.append("  ")
                .append(RenderUtils.getAlias(openClass))
                .append(" --> ")
                .append(RenderUtils.getAlias(target))
                .append(System.lineSeparator());
          }
        }
      }

      sb.append("}").append(System.lineSeparator());
    }

    // 5. Render Inter-Module Relationships (ModuleA -> ModuleB)
    for (String r : relations) {
      sb.append(r).append(System.lineSeparator());
    }

    sb.append("@enduml");
    return sb.toString();
  }

  private static @NonNull Set<JavaClass> getJavaClasses(
      String modulePkg, Set<JavaClass> openClasses) {
    Set<JavaClass> referencedClosedClasses = new HashSet<>();
    for (JavaClass openClass : openClasses) {
      for (Dependency dep : openClass.getDirectDependenciesFromSelf()) {
        JavaClass target = dep.getTargetClass();
        // Check if target is in a sub-package of the current module
        if (target.getPackageName().startsWith(modulePkg + ".")
            && !target.getPackageName().equals(modulePkg)) {
          referencedClosedClasses.add(target);
        }
      }
    }
    return referencedClosedClasses;
  }

  private static void renderClassNode(StringBuilder sb, JavaClass clazz) {
    renderClassNode(sb, clazz, "");
  }

  private static void renderClassNode(StringBuilder sb, JavaClass clazz, String stereotype) {
    String type = determineClassType(clazz);
    String alias = RenderUtils.getAlias(clazz);
    String name = RenderUtils.getSimpleName(clazz);

    sb.append("  ").append(type).append(" \"").append(name).append("\" as ").append(alias);
    if (!stereotype.isEmpty()) {
      sb.append(" ").append(stereotype);
    }
    sb.append(System.lineSeparator());
  }

  private static String determineClassType(JavaClass clazz) {
    if (clazz.isInterface()) return "interface";
    if (clazz.isEnum()) return "enum";
    return "class";
  }
}
