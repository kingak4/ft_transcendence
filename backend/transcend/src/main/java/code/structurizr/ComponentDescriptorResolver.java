package code.structurizr;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

public class ComponentDescriptorResolver {

  public String resolveComponentName(Class<?> clazz, Map<String, Integer> simpleNameCounts) {
    int duplicates = simpleNameCounts.getOrDefault(clazz.getSimpleName(), 0);
    return duplicates > 1 ? clazz.getName() : clazz.getSimpleName();
  }

  public String resolveRelativePackage(String basePackage, String packageName) {
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

  public String resolveTechnology(Class<?> clazz) {
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

  public Set<String> resolveTags(Class<?> clazz, String relativePackage) {
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
}
