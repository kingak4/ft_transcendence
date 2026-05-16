package code.archgen;

import com.structurizr.Workspace;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.view.ComponentView;
import com.structurizr.view.Styles;
import com.structurizr.view.ViewSet;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

public class StructurizrModuleExporter {

  private final ApplicationModules modules;
  private final Workspace workspace;
  private final Container container;
  private final Map<String, Component> classToComponentMap = new HashMap<>();
  private final StructurizrPlantUMLExporter exporter = new StructurizrPlantUMLExporter();

  public StructurizrModuleExporter(ApplicationModules modules) {
    this.modules = modules;
    this.workspace = new Workspace("Modulith", "Module Diagrams");
    Model model = workspace.getModel();
    this.container = model.addSoftwareSystem("Backend").addContainer("Transcend");

    configureStyles();
    populateModel();
    resolveDependencies();
  }

  private void configureStyles() {
    Styles styles = workspace.getViews().getConfiguration().getStyles();

    styles.addElementStyle("Open").background("#A9DCDF").color("#000000").stroke("#005f6b");

    styles.addElementStyle("Closed").background("#E0E0E0").color("#333333").stroke("#999999");

    styles.addElementStyle("Group").color("#444444").background("#f9f9f9");
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

  public void export(Path basePath) {
    ViewSet views = workspace.getViews();

    createOverviewView(views, basePath);
    createInternalPackageViews(views, basePath);
  }

  private void createInternalPackageViews(ViewSet views, Path basePath) {
    for (ApplicationModule module : modules) {
      Set<String> internalPackages = findInternalPackageNames(module);

      for (String pkgName : internalPackages) {
        String viewId =
            "components-" + sanitize(module.getIdentifier().toString()) + "-" + sanitize(pkgName);
        ComponentView pkgView =
            views.createComponentView(container, viewId, "Internal Pkg: " + pkgName);

        addComponentsInPackage(pkgView, pkgName);

        addNeighbors(pkgView);

        writeView(pkgView, basePath.resolve(viewId + ".puml"));
      }
    }
  }

  private Set<String> findInternalPackageNames(ApplicationModule module) {
    Set<String> internalPackages = new HashSet<>();
    String basePkgName = module.getBasePackage().getName();

    Iterable<JavaClass> classes = (Iterable<JavaClass>) module.getBasePackage().getClasses();

    for (JavaClass jc : classes) {
      String pkgName = jc.getPackageName();

      if (pkgName.startsWith(basePkgName + ".")) {
        String relativePkg = pkgName.substring(basePkgName.length() + 1);

        int firstDot = relativePkg.indexOf('.');
        String firstLevelSubPackage =
            (firstDot == -1) ? relativePkg : relativePkg.substring(0, firstDot);

        internalPackages.add(basePkgName + "." + firstLevelSubPackage);
      }
    }
    return internalPackages;
  }

  private void addComponentsInPackage(ComponentView view, String packageName) {
    for (Map.Entry<String, Component> entry : classToComponentMap.entrySet()) {
      String classFqcn = entry.getKey();
      if (classFqcn.startsWith(packageName + ".") || classFqcn.equals(packageName)) {
        view.add(entry.getValue());
      }
    }
  }

  private void createOverviewView(ViewSet views, Path basePath) {
    ComponentView overview =
        views.createComponentView(container, "packages-overview", "API Overview");

    for (Component component : container.getComponents()) {
      if (component.getTags().contains("Open")) {
        overview.add(component);
      }
    }

    addNeighbors(overview);
    writeView(overview, basePath.resolve("packages-overview.puml"));
  }

  private void addNeighbors(ComponentView view) {
    Set<Component> componentsInView = new java.util.HashSet<>();

    view.getElements()
        .forEach(
            elementView -> {
              if (elementView.getElement() instanceof Component) {
                componentsInView.add((Component) elementView.getElement());
              }
            });

    for (Component c : componentsInView) {
      view.addNearestNeighbours(c);
    }
  }

  private void writeView(ComponentView view, Path file) {
    try {
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
    } catch (IOException e) {
      throw new RuntimeException("Failed to write diagram: " + file, e);
    }
  }

  private String sanitize(String input) {
    return input.replaceAll("[^a-zA-Z0-9\\-_]", "-");
  }
}
