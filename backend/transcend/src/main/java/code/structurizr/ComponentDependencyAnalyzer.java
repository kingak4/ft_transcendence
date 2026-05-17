package code.structurizr;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ComponentDependencyAnalyzer {

  public Map<String, Set<String>> collectDependencyKinds(
      Class<?> clazz, Set<String> knownClassNames) {
    Map<String, Set<String>> dependencies = new HashMap<>();

    collectHierarchyDependencies(clazz, knownClassNames, dependencies);
    collectFieldDependencies(clazz, knownClassNames, dependencies);
    collectConstructorDependencies(clazz, knownClassNames, dependencies);
    collectMethodDependencies(clazz, knownClassNames, dependencies);

    return dependencies;
  }

  private void collectHierarchyDependencies(
      Class<?> clazz, Set<String> knownClassNames, Map<String, Set<String>> dependencies) {
    Class<?> superclass = clazz.getSuperclass();
    if (isProjectClass(superclass, knownClassNames)) {
      addDependency(dependencies, superclass.getName(), "extends");
    }

    for (Class<?> implementedInterface : clazz.getInterfaces()) {
      if (isProjectClass(implementedInterface, knownClassNames)) {
        addDependency(dependencies, implementedInterface.getName(), "implements");
      }
    }
  }

  private void collectFieldDependencies(
      Class<?> clazz, Set<String> knownClassNames, Map<String, Set<String>> dependencies) {
    for (Field field : clazz.getDeclaredFields()) {
      Set<Class<?>> referenced = new LinkedHashSet<>();
      collectReferencedClasses(field.getGenericType(), referenced);
      addTypeDependencies(dependencies, referenced, knownClassNames, "field");
    }
  }

  private void collectConstructorDependencies(
      Class<?> clazz, Set<String> knownClassNames, Map<String, Set<String>> dependencies) {
    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
      Set<Class<?>> referenced = new LinkedHashSet<>();
      for (Type parameterType : constructor.getGenericParameterTypes()) {
        collectReferencedClasses(parameterType, referenced);
      }
      addTypeDependencies(dependencies, referenced, knownClassNames, "constructor");
    }
  }

  private void collectMethodDependencies(
      Class<?> clazz, Set<String> knownClassNames, Map<String, Set<String>> dependencies) {
    for (Method method : clazz.getDeclaredMethods()) {
      Set<Class<?>> referenced = new LinkedHashSet<>();
      collectReferencedClasses(method.getGenericReturnType(), referenced);
      collectReferencedTypes(method.getGenericParameterTypes(), referenced);
      collectReferencedTypes(method.getGenericExceptionTypes(), referenced);
      addTypeDependencies(dependencies, referenced, knownClassNames, "method signature");
    }
  }

  private void collectReferencedTypes(Type[] types, Set<Class<?>> accumulator) {
    for (Type type : types) {
      collectReferencedClasses(type, accumulator);
    }
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
      collectClassReference(clazz, accumulator);
      return;
    }

    if (type instanceof ParameterizedType parameterizedType) {
      collectParameterizedTypeReferences(parameterizedType, accumulator);
      return;
    }

    if (type instanceof GenericArrayType genericArrayType) {
      collectReferencedClasses(genericArrayType.getGenericComponentType(), accumulator);
      return;
    }

    if (type instanceof WildcardType wildcardType) {
      collectReferencedTypes(wildcardType.getUpperBounds(), accumulator);
      collectReferencedTypes(wildcardType.getLowerBounds(), accumulator);
      return;
    }

    if (type instanceof TypeVariable<?> typeVariable) {
      collectReferencedTypes(typeVariable.getBounds(), accumulator);
    }
  }

  private void collectClassReference(Class<?> clazz, Set<Class<?>> accumulator) {
    if (clazz.isArray()) {
      collectReferencedClasses(clazz.getComponentType(), accumulator);
      return;
    }
    accumulator.add(clazz);
  }

  private void collectParameterizedTypeReferences(
      ParameterizedType parameterizedType, Set<Class<?>> accumulator) {
    collectReferencedClasses(parameterizedType.getRawType(), accumulator);
    collectReferencedTypes(parameterizedType.getActualTypeArguments(), accumulator);
  }

  private boolean isProjectClass(Class<?> clazz, Set<String> knownClassNames) {
    return clazz != null && knownClassNames.contains(clazz.getName());
  }

  private void addDependency(
      Map<String, Set<String>> dependencies, String className, String relationKind) {
    dependencies.computeIfAbsent(className, key -> new LinkedHashSet<>()).add(relationKind);
  }
}
