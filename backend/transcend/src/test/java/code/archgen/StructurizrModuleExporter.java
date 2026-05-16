package code.archgen;

import com.structurizr.model.Component;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ViewSet;
import com.tngtech.archunit.core.domain.JavaClass;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Exports Structurizr component views for modules and packages. The exporter separates the
 * 'overview' and 'internal package' branches and delegates rendering and asciidoc generation.
 */
public class StructurizrModuleExporter {

  private final ViewWriter renderer;
  private final ModelProvider model;
  private final AsciidocGenerator asciidocGenerator;

  public StructurizrModuleExporter(ApplicationModules modules) {
    this(new ViewWriter(), new ModelProvider(modules), new AsciidocGenerator());
  }

  public StructurizrModuleExporter(
      ViewWriter renderer, ModelProvider model, AsciidocGenerator asciidocGenerator) {
    this.renderer = renderer;
    this.model = model;
    this.asciidocGenerator = asciidocGenerator;
    StyleConfig.configureStyles(model.getWorkspace().getViews().getConfiguration().getStyles());
  }

  @SneakyThrows
  public void export(Path basePath) {
    exportOverview(basePath);
    exportInternalPackageViews(basePath);
    asciidocGenerator.generateIndex(model.getModules(), basePath);
  }

  @SneakyThrows
  public void exportOverview(Path basePath) {
    createOverviewView(model.getWorkspace().getViews(), basePath);
  }

  @SneakyThrows
  public void exportInternalPackageViews(Path basePath) {
    createInternalPackageViews(model.getWorkspace().getViews(), basePath);
  }

  private void createOverviewView(ViewSet views, Path basePath) {
    String viewId = "packages-overview";
    ComponentView view =
        createComponentView(
            views,
            viewId,
            "API Overview",
            v ->
                model.getContainer().getComponents().stream()
                    .filter(c -> c.getTags().contains("Open"))
                    .forEach(v::add));
    renderer.writeView(view, basePath.resolve(viewId + ".puml"));
  }

  private void createInternalPackageViews(ViewSet views, Path basePath) {
    for (ApplicationModule module : model.getModules()) {
      Set<String> internalPackages = findInternalPackageNames(module);

      for (String pkgName : internalPackages) {
        String viewId = DiagramUtils.buildViewId(module, pkgName);

        ComponentView view =
            createComponentView(
                views, viewId, "Internal Pkg: " + pkgName, v -> addComponentsInPackage(v, pkgName));
        renderer.writeView(view, basePath.resolve(viewId + ".puml"));
      }
    }
  }

  private ComponentView createComponentView(
      ViewSet views, String id, String description, Consumer<ComponentView> initializer) {
    ComponentView view = views.createComponentView(model.getContainer(), id, description);
    initializer.accept(view);
    addNeighbors(view);
    return view;
  }

  private Set<String> findInternalPackageNames(ApplicationModule module) {
    Set<String> internalPackages = new HashSet<>();
    String basePkgName = module.getBasePackage().getName();

    Iterable<JavaClass> classes = module.getBasePackage().getClasses();

    for (JavaClass jc : classes) {
      String pkgName = jc.getPackageName();

      if (!pkgName.startsWith(basePkgName + ".")) continue;

      String relativePkg = pkgName.substring(basePkgName.length() + 1);
      int firstDot = relativePkg.indexOf('.');
      String firstLevelSubPackage =
          (firstDot == -1) ? relativePkg : relativePkg.substring(0, firstDot);

      internalPackages.add(basePkgName + "." + firstLevelSubPackage);
    }

    return internalPackages;
  }

  private void addComponentsInPackage(ComponentView view, String packageName) {
    model.getClassToComponentMap().entrySet().stream()
        .filter(
            e -> {
              String classFqcn = e.getKey();
              return classFqcn.equals(packageName) || classFqcn.startsWith(packageName + ".");
            })
        .map(Map.Entry::getValue)
        .forEach(view::add);
  }

  private void addNeighbors(ComponentView view) {
    Set<Component> componentsInView =
        view.getElements().stream()
            .map(ev -> ev.getElement())
            .filter(e -> e instanceof Component)
            .map(e -> (Component) e)
            .collect(Collectors.toSet());

    for (Component c : componentsInView) {
      view.addNearestNeighbours(c);
    }
  }
}
