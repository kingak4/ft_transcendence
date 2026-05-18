package code.structurizr;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComponentModelBuilder {

  private final ComponentDescriptorResolver descriptorResolver;
  private final ComponentPropertyEnricher propertyEnricher;
  private final ComponentDependencyAnalyzer dependencyAnalyzer;

  public ComponentModelBuilder() {
    this.descriptorResolver = new ComponentDescriptorResolver();
    this.propertyEnricher = new ComponentPropertyEnricher();
    this.dependencyAnalyzer = new ComponentDependencyAnalyzer();
  }

  public void populate(Workspace workspace, String basePackage, Map<String, Class<?>> classes) {
    Container backendApi = findBackendApi(workspace);
    if (backendApi == null) {
      return;
    }

    if (classes.isEmpty()) {
      log.warn("No classes discovered in package {}", basePackage);
      return;
    }

    Map<String, Integer> simpleNameCounts = countSimpleNames(classes.values());
    Map<String, Component> componentsByClassName = new LinkedHashMap<>();

    addComponents(
        backendApi, basePackage, classes.values(), simpleNameCounts, componentsByClassName);

    addInferredRelationships(classes, componentsByClassName);
    addContainerMetadata(backendApi, basePackage, componentsByClassName);
  }

  private Container findBackendApi(Workspace workspace) {
    Model model = workspace.getModel();
    SoftwareSystem system = model.getSoftwareSystemWithName("Transcend");
    if (system == null) {
      return null;
    }
    return system.getContainerWithName("Backend API");
  }

  private Map<String, Integer> countSimpleNames(Iterable<Class<?>> classes) {
    Map<String, Integer> counts = new HashMap<>();
    for (Class<?> clazz : classes) {
      counts.merge(clazz.getSimpleName(), 1, Integer::sum);
    }
    return counts;
  }

  private void addComponents(
      Container backendApi,
      String basePackage,
      Iterable<Class<?>> classes,
      Map<String, Integer> simpleNameCounts,
      Map<String, Component> componentsByClassName) {
    for (Class<?> clazz : classes) {
      Component component = addComponent(backendApi, clazz, basePackage, simpleNameCounts);
      componentsByClassName.put(clazz.getName(), component);
    }
  }

  private Component addComponent(
      Container backendApi,
      Class<?> clazz,
      String basePackage,
      Map<String, Integer> simpleNameCounts) {
    String className = clazz.getName();
    String relativePackage =
        descriptorResolver.resolveRelativePackage(basePackage, clazz.getPackageName());
    String componentName = descriptorResolver.resolveComponentName(clazz, simpleNameCounts);
    String technology = descriptorResolver.resolveTechnology(clazz);

    Component component = backendApi.addComponent(componentName, className, technology);
    component.setGroup(relativePackage);
    propertyEnricher.enrich(component, clazz, relativePackage);

    Set<String> tags = descriptorResolver.resolveTags(clazz, relativePackage);
    if (!tags.isEmpty()) {
      component.addTags(tags.toArray(String[]::new));
    }
    return component;
  }

  private void addContainerMetadata(
      Container backendApi, String basePackage, Map<String, Component> componentsByClassName) {
    propertyEnricher.addPropertyIfPresent(backendApi, "basePackage", basePackage);
    propertyEnricher.addPropertyIfPresent(
        backendApi, "componentCount", String.valueOf(componentsByClassName.size()));
    propertyEnricher.addPropertyIfPresent(
        backendApi,
        "packageCount",
        String.valueOf(
            componentsByClassName.values().stream()
                .map(Component::getGroup)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                .size()));
  }

  private void addInferredRelationships(
      Map<String, Class<?>> classes, Map<String, Component> componentsByClassName) {
    Set<String> addedRelations = new HashSet<>();

    for (Map.Entry<String, Class<?>> sourceEntry : classes.entrySet()) {
      String sourceClassName = sourceEntry.getKey();
      Class<?> sourceClass = sourceEntry.getValue();
      Component sourceComponent = componentsByClassName.get(sourceClassName);
      if (sourceComponent == null) {
        continue;
      }

      Map<String, Set<String>> dependencies =
          dependencyAnalyzer.collectDependencyKinds(sourceClass, classes.keySet());
      for (Map.Entry<String, Set<String>> dependency : dependencies.entrySet()) {
        String targetClassName = dependency.getKey();
        if (sourceClassName.equals(targetClassName)) {
          continue;
        }

        Component targetComponent = componentsByClassName.get(targetClassName);
        if (targetComponent == null) {
          continue;
        }

        String relationKey = sourceClassName + "->" + targetClassName;
        if (!addedRelations.add(relationKey)) {
          continue;
        }

        String description =
            dependency.getValue().isEmpty()
                ? "Depends on"
                : "Depends on (" + String.join(", ", new TreeSet<>(dependency.getValue())) + ")";

        sourceComponent.uses(targetComponent, description, "Java");
      }
    }
  }
}
