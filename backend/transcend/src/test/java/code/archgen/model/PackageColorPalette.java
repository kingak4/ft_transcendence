package code.archgen.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates a consistent color palette for packages. Ensures same package always gets same color
 * across diagrams.
 */
public class PackageColorPalette {

  // Color palette (12 distinct colors)
  private static final String COLOR_RED = "#FF6B6B";
  private static final String COLOR_TEAL = "#4ECDC4";
  private static final String COLOR_BLUE = "#45B7D1";
  private static final String COLOR_SALMON = "#FFA07A";
  private static final String COLOR_MINT = "#98D8C8";
  private static final String COLOR_YELLOW = "#F7DC6F";
  private static final String COLOR_PURPLE = "#BB8FCE";
  private static final String COLOR_LIGHT_BLUE = "#85C1E2";
  private static final String COLOR_PEACH = "#F8B88B";
  private static final String COLOR_GREEN = "#A9DFBF";
  private static final String COLOR_LIGHT_RED = "#F5B7B1";
  private static final String COLOR_LAVENDER = "#D7BDE2";

  private static final String[] COLORS = {
    COLOR_RED,
    COLOR_TEAL,
    COLOR_BLUE,
    COLOR_SALMON,
    COLOR_MINT,
    COLOR_YELLOW,
    COLOR_PURPLE,
    COLOR_LIGHT_BLUE,
    COLOR_PEACH,
    COLOR_GREEN,
    COLOR_LIGHT_RED,
    COLOR_LAVENDER
  };

  private final Map<String, String> packageToColor = new HashMap<>();

  /** Get a color for the given package name. Returns the same color consistently. */
  public String getColorForPackage(String packageName) {
    return packageToColor.computeIfAbsent(
        packageName, key -> COLORS[packageToColor.size() % COLORS.length]);
  }

  public Map<String, String> getPackageColorMap() {
    return new HashMap<>(packageToColor);
  }
}
