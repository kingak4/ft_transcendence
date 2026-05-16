package code.archgen;

import org.springframework.modulith.core.ApplicationModule;

class DiagramUtils {
  public static String sanitize(String input) {
    return input.replaceAll("[^a-zA-Z0-9\\-_]", "-");
  }

  public static String buildViewId(ApplicationModule module, String pkgName) {
    return "components-"
        + DiagramUtils.sanitize(module.getIdentifier().toString())
        + "-"
        + DiagramUtils.sanitize(pkgName);
  }
}
