package code.structurizr;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    contextView.enableAutomaticLayout();

    ContainerView containerView =
        views.createContainerView(system, "Containers", "Container Diagram");
    containerView.addAllContainers();
    containerView.enableAutomaticLayout();
  }

  private void createComponentViews(ViewSet views, Container backendApi) {
    ComponentView allComponentsView =
        views.createComponentView(backendApi, "Components", "Component Diagram");
    allComponentsView.addAllComponents();
    allComponentsView.enableAutomaticLayout();

    Map<String, List<com.structurizr.model.Component>> byTopLevelPackage =
        groupByTopLevelPackage(backendApi);

    for (Map.Entry<String, List<com.structurizr.model.Component>> entry :
        byTopLevelPackage.entrySet()) {
      String topLevelPackage = entry.getKey();
      List<com.structurizr.model.Component> components = entry.getValue();
      if (components.isEmpty()) {
        continue;
      }

      ComponentView packageView =
          views.createComponentView(
              backendApi,
              "Components_" + sanitizeKey(topLevelPackage),
              "Component dependencies centered on package: " + topLevelPackage);

      for (com.structurizr.model.Component component : components) {
        packageView.add(component);
        packageView.addNearestNeighbours(component);
      }
      packageView.enableAutomaticLayout();
    }
  }

  private Map<String, List<com.structurizr.model.Component>> groupByTopLevelPackage(
      Container container) {
    Map<String, List<com.structurizr.model.Component>> byTopLevelPackage = new LinkedHashMap<>();

    for (com.structurizr.model.Component component : container.getComponents()) {
      String group = component.getGroup();
      String topLevelPackage = "default";
      if (group != null && !group.isBlank()) {
        int separator = group.indexOf('.');
        topLevelPackage = separator < 0 ? group : group.substring(0, separator);
      }
      byTopLevelPackage.computeIfAbsent(topLevelPackage, key -> new ArrayList<>()).add(component);
    }

    return byTopLevelPackage;
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
