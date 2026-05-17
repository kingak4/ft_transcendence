package code.structurizr;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Slf4j
public class ComponentModelBuilder {

  public void populate(Workspace workspace, String basePackage, Map<String, Class<?>> classes) {
    Model model = workspace.getModel();
    SoftwareSystem system = model.getSoftwareSystemWithName("Transcend");
    if (system == null) {
      return;
    }

    Container backendApi = system.getContainerWithName("Backend API");
    if (backendApi == null) {
      return;
    }

    if (classes.isEmpty()) {
      log.warn("No classes discovered in package {}", basePackage);
      return;
    }

    Map<String, Integer> simpleNameCounts = countSimpleNames(classes.values());
    Map<String, com.structurizr.model.Component> componentsByClassName = new LinkedHashMap<>();

    for (Class<?> clazz : classes.values()) {
      com.structurizr.model.Component component =
          addComponent(backendApi, clazz, basePackage, simpleNameCounts);
      componentsByClassName.put(clazz.getName(), component);
    }

    addInferredRelationships(classes, componentsByClassName);
    addContainerMetadata(backendApi, basePackage, componentsByClassName);
  }

  private Map<String, Integer> countSimpleNames(Iterable<Class<?>> classes) {
    Map<String, Integer> counts = new HashMap<>();
    for (Class<?> clazz : classes) {
      counts.merge(clazz.getSimpleName(), 1, Integer::sum);
    }
    return counts;
  }

  private com.structurizr.model.Component addComponent(
      Container backendApi, Class<?> clazz, String basePackage, Map<String, Integer> simpleNameCounts) {
    String className = clazz.getName();
    String relativePackage = getRelativePackage(basePackage, clazz.getPackageName());
    String componentName =
        simpleNameCounts.getOrDefault(clazz.getSimpleName(), 0) > 1
            ? className
            : clazz.getSimpleName();

    com.structurizr.model.Component component =
        backendApi.addComponent(componentName, className, determineTechnology(clazz));
    component.setGroup(relativePackage);

    addPropertyIfPresent(component, "className", className);
    addPropertyIfPresent(component, "package", clazz.getPackageName());
    addPropertyIfPresent(component, "simpleName", clazz.getSimpleName());
    addPropertyIfPresent(component, "relativePackage", relativePackage);
    addPropertyIfPresent(component, "modifiers", Modifier.toString(clazz.getModifiers()));
    addPropertyIfPresent(component, "isAbstract", String.valueOf(Modifier.isAbstract(clazz.getModifiers())));
    addPropertyIfPresent(component, "isFinal", String.valueOf(Modifier.isFinal(clazz.getModifiers())));
    addPropertyIfPresent(component, "isRecord", String.valueOf(clazz.isRecord()));
    addPropertyIfPresent(component, "annotations", toAnnotationList(clazz));
    addPropertyIfPresent(
        component,
        "extends",
        clazz.getSuperclass() != null ? clazz.getSuperclass().getName() : null);
    addPropertyIfPresent(component, "implements", toInterfaceList(clazz));

    Set<String> tags = determineTags(clazz, relativePackage);
    if (!tags.isEmpty()) {
      component.addTags(tags.toArray(String[]::new));
    }
    return component;
  }

  private String toAnnotationList(Class<?> clazz) {
    return List.of(clazz.getAnnotations()).stream()
        .map(annotation -> annotation.annotationType().getSimpleName())
        .sorted()
        .collect(Collectors.joining(","));
  }

  private String toInterfaceList(Class<?> clazz) {
    return List.of(clazz.getInterfaces()).stream()
        .map(Class::getName)
        .sorted()
        .collect(Collectors.joining(","));
  }

  private void addContainerMetadata(
      Container backendApi,
      String basePackage,
      Map<String, com.structurizr.model.Component> componentsByClassName) {
    addPropertyIfPresent(backendApi, "basePackage", basePackage);
    addPropertyIfPresent(backendApi, "componentCount", String.valueOf(componentsByClassName.size()));
    addPropertyIfPresent(
        backendApi,
        "packageCount",
        String.valueOf(
            componentsByClassName.values().stream()
                .map(com.structurizr.model.Component::getGroup)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                .size()));
  }

  private void addInferredRelationships(
      Map<String, Class<?>> classes,
      Map<String, com.structurizr.model.Component> componentsByClassName) {
    Set<String> addedRelations = new HashSet<>();

    for (Map.Entry<String, Class<?>> sourceEntry : classes.entrySet()) {
      String sourceClassName = sourceEntry.getKey();
      Class<?> sourceClass = sourceEntry.getValue();
      com.structurizr.model.Component sourceComponent = componentsByClassName.get(sourceClassName);
      if (sourceComponent == null) {
        continue;
      }

      Map<String, Set<String>> dependencies = collectDependencyKinds(sourceClass, classes.keySet());
      for (Map.Entry<String, Set<String>> dependency : dependencies.entrySet()) {
        String targetClassName = dependency.getKey();
        if (sourceClassName.equals(targetClassName)) {
          continue;
        }

        com.structurizr.model.Component targetComponent = componentsByClassName.get(targetClassName);
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

  private Map<String, Set<String>> collectDependencyKinds(
      Class<?> clazz, Set<String> knownClassNames) {
    Map<String, Set<String>> dependencies = new HashMap<>();

    Class<?> superclass = clazz.getSuperclass();
    if (isProjectClass(superclass, knownClassNames)) {
      addDependency(dependencies, superclass.getName(), "extends");
    }

    for (Class<?> implementedInterface : clazz.getInterfaces()) {
      if (isProjectClass(implementedInterface, knownClassNames)) {
        addDependency(dependencies, implementedInterface.getName(), "implements");
      }
    }

    for (Field field : clazz.getDeclaredFields()) {
      Set<Class<?>> referenced = new LinkedHashSet<>();
      collectReferencedClasses(field.getGenericType(), referenced);
      addTypeDependencies(dependencies, referenced, knownClassNames, "field");
    }

    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      Set<Class<?>> referenced = new LinkedHashSet<>();
      for (Type parameterType : constructor.getGenericParameterTypes()) {
        collectReferencedClasses(parameterType, referenced);
      }
      addTypeDependencies(dependencies, referenced, knownClassNames, "constructor");
    }

    for (Method method : clazz.getDeclaredMethods()) {
      Set<Class<?>> referenced = new LinkedHashSet<>();
      collectReferencedClasses(method.getGenericReturnType(), referenced);
      for (Type parameterType : method.getGenericParameterTypes()) {
        collectReferencedClasses(parameterType, referenced);
      }
      for (Type exceptionType : method.getGenericExceptionTypes()) {
        collectReferencedClasses(exceptionType, referenced);
      }
      addTypeDependencies(dependencies, referenced, knownClassNames, "method signature");
    }

    return dependencies;
  }

  private void addTypeDependencies(
      Map<String, Set<String>> dependencies,
      Set<Class<?>> referenced,
      Set<String> knownClassNames,
      String relationKind) {
    for (Class<?> dependencyClass : referenced) {
      if (isProjectClass(dependencyClass, knownClassNames)) {
        addDependency(dependencies, dependencyClass.getName(), relationKind);
      }
    }
  }

  private void collectReferencedClasses(Type type, Set<Class<?>> accumulator) {
    if (type == null) {
      return;
    }

    if (type instanceof Class<?> clazz) {
      if (clazz.isArray()) {
        collectReferencedClasses(clazz.getComponentType(), accumulator);
        return;
      }
      accumulator.add(clazz);
      return;
    }

    if (type instanceof ParameterizedType parameterizedType) {
      collectReferencedClasses(parameterizedType.getRawType(), accumulator);
      for (Type argument : parameterizedType.getActualTypeArguments()) {
        collectReferencedClasses(argument, accumulator);
      }
      return;
    }

    if (type instanceof GenericArrayType genericArrayType) {
      collectReferencedClasses(genericArrayType.getGenericComponentType(), accumulator);
      return;
    }

    if (type instanceof WildcardType wildcardType) {
      for (Type upperBound : wildcardType.getUpperBounds()) {
        collectReferencedClasses(upperBound, accumulator);
      }
      for (Type lowerBound : wildcardType.getLowerBounds()) {
        collectReferencedClasses(lowerBound, accumulator);
      }
      return;
    }

    if (type instanceof TypeVariable<?> typeVariable) {
      for (Type bound : typeVariable.getBounds()) {
        collectReferencedClasses(bound, accumulator);
      }
    }
  }

  private boolean isProjectClass(Class<?> clazz, Set<String> knownClassNames) {
    return clazz != null && knownClassNames.contains(clazz.getName());
  }

  private void addDependency(Map<String, Set<String>> dependencies, String className, String relationKind) {
    dependencies.computeIfAbsent(className, key -> new LinkedHashSet<>()).add(relationKind);
  }

  private String determineTechnology(Class<?> clazz) {
    if (clazz.isAnnotationPresent(Controller.class)) {
      return "Spring MVC Controller";
    }
    if (clazz.isAnnotationPresent(Service.class)) {
      return "Spring Service";
    }
    if (clazz.isAnnotationPresent(Repository.class)) {
      return "Spring Data Repository";
    }
    if (clazz.isAnnotationPresent(org.springframework.stereotype.Component.class)) {
      return "Spring Component";
    }
    if (clazz.getName().contains(".domain.")) {
      return "Domain Model";
    }
    if (clazz.getName().contains(".ports.")) {
      return "Port";
    }
    if (clazz.getName().contains(".infrastructure.")) {
      return "Infrastructure";
    }
    if (clazz.getName().contains(".entrypoints.")) {
      return "Entrypoint";
    }
    return "Java";
  }

  private Set<String> determineTags(Class<?> clazz, String relativePackage) {
    Set<String> tags = new LinkedHashSet<>();
    tags.add("Class");
    tags.add("Package:" + relativePackage);

    if (clazz.isAnnotationPresent(Controller.class)) {
      tags.add("Controller");
    }
    if (clazz.isAnnotationPresent(Service.class)) {
      tags.add("Service");
    }
    if (clazz.isAnnotationPresent(Repository.class)) {
      tags.add("Repository");
    }
    if (clazz.isAnnotationPresent(org.springframework.stereotype.Component.class)) {
      tags.add("Component");
    }
    if (clazz.getName().contains("Config") || clazz.getName().contains("Configuration")) {
      tags.add("Configuration");
    }
    if (clazz.getName().contains("Adapter")) {
      tags.add("Adapter");
    }
    if (clazz.getName().contains("UseCase")) {
      tags.add("UseCase");
    }
    if (clazz.getName().contains("Port")) {
      tags.add("Port");
    }

    return tags;
  }

  private String getRelativePackage(String basePackage, String packageName) {
    if (packageName == null || packageName.isBlank()) {
      return "default";
    }
    if (packageName.equals(basePackage)) {
      return "default";
    }
    if (packageName.startsWith(basePackage + ".")) {
      return packageName.substring(basePackage.length() + 1);
    }
    return packageName;
  }

  private void addPropertyIfPresent(com.structurizr.model.StaticStructureElement element, String key, String value) {
    if (value != null && !value.isBlank()) {
      element.addProperty(key, value);
    }
  }
}
