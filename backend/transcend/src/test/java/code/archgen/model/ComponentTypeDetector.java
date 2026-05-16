package code.archgen.model;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;

/**
 * Detects and tags Java component types (Interface, Enum, Annotation, Abstract Class, Concrete
 * Class).
 */
public class ComponentTypeDetector {

  public static String detectType(JavaClass javaClass) {
    if (javaClass.isAnnotation()) {
      return ArchgenTags.TAG_JAVA_ANNOTATION;
    }
    if (javaClass.isInterface()) {
      return ArchgenTags.TAG_JAVA_INTERFACE;
    }
    if (javaClass.isEnum()) {
      return ArchgenTags.TAG_JAVA_ENUM;
    }
    if (javaClass.getModifiers().contains(JavaModifier.ABSTRACT)) {
      return ArchgenTags.TAG_JAVA_ABSTRACT;
    }
    return ArchgenTags.TAG_JAVA_CLASS;
  }
}
