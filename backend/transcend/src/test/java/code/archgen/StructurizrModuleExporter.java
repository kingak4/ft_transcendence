package code.archgen;

import com.structurizr.model.Component;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ViewSet;
import com.tngtech.archunit.core.domain.JavaClass;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

public class StructurizrModuleExporter {

  private final DiagramRenderer renderer;
  private final ModelProvider model;

  public StructurizrModuleExporter(ApplicationModules modules) {
    renderer = new DiagramRenderer();
    model = new ModelProvider(modules);
    StyleConfig.configureStyles(model.getWorkspace().getViews().getConfiguration().getStyles());
  }

  @SneakyThrows
  public void export(Path basePath) {
    ViewSet views = model.getWorkspace().getViews();

    createOverviewView(views, basePath);
    createInternalPackageViews(views, basePath);
    renderer.generateAsciidocIndex(
        model.getModules().stream()
            .sorted(Comparator.comparing(m -> m.getIdentifier().toString()))
            .toList(),
        basePath);
  }

  private void createOverviewView(ViewSet views, Path basePath) {
    ComponentView overview =
        views.createComponentView(model.getContainer(), "packages-overview", "API Overview");

    for (Component component : model.getContainer().getComponents()) {
      if (component.getTags().contains("Open")) {
        overview.add(component);
      }
    }

    addNeighbors(overview);
    renderer.writeView(overview, basePath.resolve("packages-overview.puml"));
  }

  private void createInternalPackageViews(ViewSet views, Path basePath) {
    for (ApplicationModule module : model.getModules()) {
      Set<String> internalPackages = findInternalPackageNames(module);

      for (String pkgName : internalPackages) {
        String viewId =
            "components-"
                + DiagramUtils.sanitize(module.getIdentifier().toString())
                + "-"
                + DiagramUtils.sanitize(pkgName);
        ComponentView pkgView =
            views.createComponentView(model.getContainer(), viewId, "Internal Pkg: " + pkgName);

        addComponentsInPackage(pkgView, pkgName);

        addNeighbors(pkgView);

        renderer.writeView(pkgView, basePath.resolve(viewId + ".puml"));
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
    for (Map.Entry<String, Component> entry : model.getClassToComponentMap().entrySet()) {
      String classFqcn = entry.getKey();
      if (classFqcn.startsWith(packageName + ".") || classFqcn.equals(packageName)) {
        view.add(entry.getValue());
      }
    }
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
}
