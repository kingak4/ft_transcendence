package code.structurizr;

import com.structurizr.model.Component;
import com.structurizr.model.StaticStructureElement;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentPropertyEnricher {

  public void enrich(Component component, Class<?> clazz, String relativePackage) {
    addPropertyIfPresent(component, "className", clazz.getName());
    addPropertyIfPresent(component, "package", clazz.getPackageName());
    addPropertyIfPresent(component, "simpleName", clazz.getSimpleName());
    addPropertyIfPresent(component, "relativePackage", relativePackage);
    addPropertyIfPresent(component, "modifiers", Modifier.toString(clazz.getModifiers()));
    addPropertyIfPresent(
        component, "isAbstract", String.valueOf(Modifier.isAbstract(clazz.getModifiers())));
    addPropertyIfPresent(
        component, "isFinal", String.valueOf(Modifier.isFinal(clazz.getModifiers())));
    addPropertyIfPresent(component, "isRecord", String.valueOf(clazz.isRecord()));
    addPropertyIfPresent(component, "annotations", toAnnotationList(clazz));
    addPropertyIfPresent(component, "extends", toSuperclassName(clazz));
    addPropertyIfPresent(component, "implements", toInterfaceList(clazz));
  }

  public void addPropertyIfPresent(StaticStructureElement element, String key, String value) {
    if (value != null && !value.isBlank()) {
      element.addProperty(key, value);
    }
  }

  private String toAnnotationList(Class<?> clazz) {
    return List.of(clazz.getAnnotations()).stream()
        .map(annotation -> annotation.annotationType().getSimpleName())
        .sorted()
        .collect(Collectors.joining(","));
  }

  private String toSuperclassName(Class<?> clazz) {
    Class<?> superclass = clazz.getSuperclass();
    return superclass != null ? superclass.getName() : null;
  }

  private String toInterfaceList(Class<?> clazz) {
    return List.of(clazz.getInterfaces()).stream()
        .map(Class::getName)
        .sorted()
        .collect(Collectors.joining(","));
  }
}
