package code.structurizr;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

@Slf4j
public class ClasspathClassDiscovery {

  public Map<String, Class<?>> discover(String basePackage) {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> true);

    Map<String, Class<?>> classes = new LinkedHashMap<>();
    for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
      String className = beanDefinition.getBeanClassName();
      if (className == null || className.contains("$")) {
        continue;
      }

      try {
        Class<?> clazz = Class.forName(className);
        if (!clazz.getPackageName().startsWith(basePackage)) {
          continue;
        }
        if (clazz.isAnnotation() || clazz.isEnum() || clazz.isInterface()) {
          continue;
        }
        classes.put(clazz.getName(), clazz);
      } catch (Throwable exception) {
        log.warn("Could not inspect class {}: {}", className, exception.getMessage());
      }
    }

    return classes.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (left, right) -> left, LinkedHashMap::new));
  }
}
