package code.structurizr;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.Dimensions;
import com.structurizr.view.PaperSize;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ViewConfigurationBuilder {

  public void configure(Workspace workspace) {
    Model model = workspace.getModel();
    SoftwareSystem system = model.getSoftwareSystemWithName("Transcend");
    if (system == null) {
      return;
    }

    ViewSet views = workspace.getViews();
    createContextAndContainerViews(views, system);

    Container backendApi = system.getContainerWithName("Backend API");
    if (backendApi != null) {
      createComponentViews(views, backendApi);
    }

    configureStyles(views);
  }

  private void createContextAndContainerViews(ViewSet views, SoftwareSystem system) {
    SystemContextView contextView =
        views.createSystemContextView(system, "SystemContext", "System Context");
    contextView.addAllSoftwareSystems();
    contextView.addAllPeople();
    contextView.setPaperSize(PaperSize.A1_Landscape);

    ContainerView containerView =
        views.createContainerView(system, "Containers", "Container Diagram");
    containerView.addAllContainers();
    containerView.setPaperSize(PaperSize.A1_Landscape);
  }

  private void createComponentViews(ViewSet views, Container backendApi) {
    //    Map<String, List<com.structurizr.model.Component>> byTopLevelPackage =
    //        groupByPackageDepth(backendApi, 1);

    //    createPackageComponentViews(views, backendApi, byTopLevelPackage, false);

    Map<String, List<com.structurizr.model.Component>> byFirstNestedPackage =
        groupByPackageDepth(backendApi, 2);

    createPackageComponentViews(views, backendApi, byFirstNestedPackage, false);
  }

  private void createPackageComponentViews(
      ViewSet views,
      Container backendApi,
      Map<String, List<Component>> byPackage,
      boolean nestedPackagesOnly) {
    for (Map.Entry<String, List<Component>> entry : byPackage.entrySet()) {
      String packageName = entry.getKey();
      List<Component> components = entry.getValue();
      if (components.isEmpty() || nestedPackagesOnly && !packageName.contains(".")) {
        continue;
      }

      createPackageComponentView(
          views,
          backendApi,
          "Package_" + sanitizeKey(packageName),
          "Package components: " + packageName,
          "Components in package \"" + packageName + "\" plus directly connected components",
          components);
    }
  }

  private void createPackageComponentView(
      ViewSet views,
      Container backendApi,
      String key,
      String title,
      String description,
      List<com.structurizr.model.Component> components) {
    ComponentView packageView = views.createComponentView(backendApi, key, description);
    packageView.setTitle(title);
    addComponentsWithDirectNeighbours(packageView, backendApi, components);
    packageView.setDimensions(new Dimensions(1000, 1000));
  }

  private void addComponentsWithDirectNeighbours(
      ComponentView view,
      Container container,
      List<com.structurizr.model.Component> selectedComponents) {
    Set<com.structurizr.model.Component> selected = Set.copyOf(selectedComponents);

    for (com.structurizr.model.Component component : selectedComponents) {
      view.add(component);
    }

    for (com.structurizr.model.Component sourceComponent : container.getComponents()) {
      for (var relationship : sourceComponent.getRelationships()) {
        if (!(relationship.getDestination()
            instanceof com.structurizr.model.Component targetComponent)) {
          continue;
        }

        boolean outgoingFromSelected = selected.contains(sourceComponent);
        boolean incomingToSelected = selected.contains(targetComponent);

        if (outgoingFromSelected || incomingToSelected) {
          view.add(sourceComponent);
          view.add(targetComponent);
        }
      }
    }
  }

  private Map<String, List<com.structurizr.model.Component>> groupByPackageDepth(
      Container container, int depth) {
    Map<String, List<com.structurizr.model.Component>> byPackage = new TreeMap<>();

    for (com.structurizr.model.Component component : container.getComponents()) {
      String packageName = packagePrefix(component.getGroup(), depth);
      byPackage.computeIfAbsent(packageName, key -> new ArrayList<>()).add(component);
    }

    return byPackage;
  }

  private String packagePrefix(String group, int depth) {
    if (group == null || group.isBlank()) {
      return "default";
    }

    String[] parts = group.split("\\.");
    int limit = Math.min(depth, parts.length);

    return java.util.Arrays.stream(parts).limit(limit).collect(Collectors.joining("."));
  }

  private void configureStyles(ViewSet views) {
    var styles = views.getConfiguration().getStyles();
    styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff");
    styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
    styles.addElementStyle(Tags.CONTAINER).background("#438dd5").color("#ffffff");
    styles.addElementStyle(Tags.COMPONENT).background("#85bbf0").color("#000000");

    styles.addElementStyle("Controller").background("#d35400").color("#ffffff");
    styles.addElementStyle("Service").background("#1f618d").color("#ffffff");
    styles.addElementStyle("Repository").background("#117864").color("#ffffff");
    styles.addElementStyle("Configuration").background("#7d3c98").color("#ffffff");
  }

  private String sanitizeKey(String raw) {
    String sanitized = raw.replaceAll("[^a-zA-Z0-9_]", "_");
    return sanitized.isBlank() ? "default" : sanitized;
  }
}
