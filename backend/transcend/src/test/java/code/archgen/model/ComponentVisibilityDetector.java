package code.archgen.model;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;

/** Detects and tags Java visibility levels (public, protected, private, package-private). */
public class ComponentVisibilityDetector {

  public static String detectVisibility(JavaClass javaClass) {
    if (javaClass.getModifiers().contains(JavaModifier.PUBLIC)) {
      return ArchgenTags.TAG_VISIBILITY_PUBLIC;
    }
    if (javaClass.getModifiers().contains(JavaModifier.PROTECTED)) {
      return ArchgenTags.TAG_VISIBILITY_PROTECTED;
    }
    if (javaClass.getModifiers().contains(JavaModifier.PRIVATE)) {
      return ArchgenTags.TAG_VISIBILITY_PRIVATE;
    }
    return ArchgenTags.TAG_VISIBILITY_PACKAGE;
  }
}
