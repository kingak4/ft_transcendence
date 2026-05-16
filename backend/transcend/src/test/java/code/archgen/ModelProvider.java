package code.archgen;

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
class ModelProvider {

  private final ApplicationModules modules;
  private final Workspace workspace;
  private final Container container;
  private final Map<String, Component> classToComponentMap = new HashMap<>();

  public ModelProvider(ApplicationModules modules) {
    this.modules = modules;
    this.workspace = new Workspace("Modulith", "Module Diagrams");
    Model model = workspace.getModel();
    this.container = model.addSoftwareSystem("Backend").addContainer("Transcend");
    populateModel();
    resolveDependencies();
  }

  private void populateModel() {
    for (ApplicationModule module : modules) {
      String basePkgName = module.getBasePackage().getName();
      Iterable<JavaClass> classes = (Iterable<JavaClass>) module.getBasePackage().getClasses();

      for (JavaClass javaClass : classes) {
        String simpleName = javaClass.getSimpleName();
        if (simpleName.equals("package-info") || simpleName.isEmpty()) continue;

        String componentName = simpleName;
        if (container.getComponentWithName(componentName) != null) {
          componentName = javaClass.getName();
        }

        Component c = container.addComponent(componentName, javaClass.getName());
        c.setTechnology("Java Class");

        String pkgName = javaClass.getPackageName();
        String groupName = module.getDisplayName();

        if (pkgName.startsWith(basePkgName + ".")) {
          String relativePkg = pkgName.substring(basePkgName.length() + 1);
          int firstDot = relativePkg.indexOf('.');
          String subFolder = (firstDot == -1) ? relativePkg : relativePkg.substring(0, firstDot);

          groupName = String.format("%s [%s]", module.getDisplayName(), subFolder);
        }
        c.setGroup(groupName);

        classToComponentMap.put(javaClass.getName(), c);
        c.addTags(module.getIdentifier().toString());
        c.addTags(module.isExposed(javaClass) ? "Open" : "Closed");
      }
    }
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
}
