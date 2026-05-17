package code.archgen.model;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

@Getter
public class ModelProvider {

  private final ApplicationModules modules;
  private final Workspace workspace;
  private final Container container;
  private final Map<String, Component> classToComponentMap = new HashMap<>();
  private final PackageColorPalette packageColorPalette = new PackageColorPalette();

  public ModelProvider(ApplicationModules modules) {
    this.modules = modules;
    this.workspace = new Workspace("Modulith", "Module Diagrams");

    Model model = workspace.getModel();
    this.container = model.addSoftwareSystem("Backend").addContainer("Transcend");

    buildModel();
  }

  private void buildModel() {
    for (ApplicationModule module : modules) {
      processModuleClasses(module);
    }
    resolveDependencies();
  }

  private void processModuleClasses(ApplicationModule module) {
    Iterable<JavaClass> classes = module.getBasePackage().getClasses();
    String moduleId = module.getIdentifier().toString();

    for (JavaClass javaClass : classes) {
      if (isIgnorable(javaClass)) continue;

      Component component = createComponent(module, javaClass);

      component.setGroup(calculateGroupName(module, javaClass));

      // Add module identification tag (no color)
      String moduleTag = ArchgenTags.TAG_MODULE_PREFIX + moduleId;
      component.addTags(moduleTag);

      // Add open/closed tag (modulith context)
      component.addTags(
          module.isExposed(javaClass) ? ArchgenTags.TAG_OPEN : ArchgenTags.TAG_CLOSED);

      // Add Java type tag
      String typeTag = ComponentTypeDetector.detectType(javaClass);
      component.addTags(typeTag);

      // Add visibility tag
      String visibilityTag = ComponentVisibilityDetector.detectVisibility(javaClass);
      component.addTags(visibilityTag);

      // Add package tag (first-level subpackage) with color
      String pkgTag =
          PackageTagExtractor.extractPackageTag(
              module.getBasePackage().getName(), javaClass.getName());
      if (pkgTag != null) {
        component.addTags(pkgTag);
        // Assign color to this package
        packageColorPalette.getColorForPackage(pkgTag);
      }

      classToComponentMap.put(javaClass.getName(), component);
    }
  }

  private Component createComponent(ApplicationModule module, JavaClass javaClass) {
    String name = javaClass.getSimpleName();

    if (container.getComponentWithName(name) != null) {
      name = javaClass.getName();
    }

    Component c = container.addComponent(name, javaClass.getName());
    c.setTechnology("Java Class");
    return c;
  }

  private String calculateGroupName(ApplicationModule module, JavaClass javaClass) {
    String basePkg = module.getBasePackage().getName();
    String pkgName = javaClass.getPackageName();

    if (pkgName.startsWith(basePkg + ".")) {
      String relativePkg = pkgName.substring(basePkg.length() + 1);
      int firstDot = relativePkg.indexOf('.');
      String subFolder = (firstDot == -1) ? relativePkg : relativePkg.substring(0, firstDot);

      return String.format("%s [%s]", module.getDisplayName(), subFolder);
    }

    return module.getDisplayName();
  }

  private void resolveDependencies() {
    for (ApplicationModule module : modules) {
      Iterable<JavaClass> classes = module.getBasePackage().getClasses();

      for (JavaClass javaClass : classes) {
        Component source = classToComponentMap.get(javaClass.getName());
        if (source == null) continue;

        for (Dependency dep : javaClass.getDirectDependenciesFromSelf()) {
          Component target = classToComponentMap.get(dep.getTargetClass().getName());

          if (target != null && !source.equals(target)) {
            source.uses(target, "");
          }
        }
      }
    }
  }

  private boolean isIgnorable(JavaClass javaClass) {
    String simpleName = javaClass.getSimpleName();
    return simpleName.equals("package-info") || simpleName.isEmpty();
  }
}
