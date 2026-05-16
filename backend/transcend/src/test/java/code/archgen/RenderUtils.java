package code.archgen;

import com.tngtech.archunit.core.domain.JavaClass;
import java.util.HashMap;
import java.util.Map;

class RenderUtils {
  private static final Map<String, String> aliasMap = new HashMap<>();

  public static String getAlias(JavaClass javaClass) {
    return getAlias(javaClass.getName());
  }

  public static String getAlias(String fullyQualifiedName) {
    return aliasMap.computeIfAbsent(fullyQualifiedName, name -> name.replace(".", "_"));
  }

  public static String getSimpleName(JavaClass javaClass) {
    return javaClass.getSimpleName();
  }

  public static String sanitizeFileName(String rawName) {
    return rawName.replaceAll("[^a-zA-Z0-9\\-_]", "-");
  }

  public static String extractRelativeFolderName(String packageName, String basePackage) {
    if (packageName.equals(basePackage)) {
      return "";
    }
    if (!packageName.startsWith(basePackage + ".")) {
      return "";
    }
    return packageName.substring(basePackage.length() + 1);
  }
}
