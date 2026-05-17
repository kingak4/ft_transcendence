package code.archgen.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates a consistent color palette for packages. Ensures same package always gets same color
 * across diagrams.
 */
public class PackageColorPalette {

  private final Map<String, String> packageToColor = new HashMap<>();

  public String getColorForPackage(String packageName) {
    return packageToColor.computeIfAbsent(
        packageName,
        key -> {
          float hue = (float) ((key.hashCode() & 0x7FFFFFFF) * 0.618033988749895 % 1.0);
          int colorInt = java.awt.Color.HSBtoRGB(hue, 0.6f, 0.9f);
          return String.format("#%06X", (0xFFFFFF & colorInt));
        });
  }

  public Map<String, String> getPackageColorMap() {
    return new HashMap<>(packageToColor);
  }
}
