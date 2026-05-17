package code.archgen.model;

import com.structurizr.view.Styles;
import java.util.Map;

public class StyleConfig {

  // Color constants
  private static final String COLOR_GROUP_TEXT = "#444444";
  private static final String COLOR_GROUP_BG = "#f9f9f9";
  private static final String COLOR_VISIBILITY_PUBLIC = "#000000";
  private static final String COLOR_VISIBILITY_PROTECTED = "#666666";
  private static final String COLOR_VISIBILITY_PRIVATE = "#CCCCCC";
  private static final String COLOR_VISIBILITY_PACKAGE = "#999999";
  private static final String COLOR_MODULE_TEXT = "#FFFFFF";

  public static void configureStyles(Styles styles, PackageColorPalette packageColorPalette) {
    // Open/Closed visibility (modulith context)
    styles.addElementStyle(ArchgenTags.TAG_OPEN).opacity(100);
    styles.addElementStyle(ArchgenTags.TAG_CLOSED).opacity(70);

    // Group styling
    styles
        .addElementStyle(ArchgenTags.TAG_GROUP)
        .color(COLOR_GROUP_TEXT)
        .background(COLOR_GROUP_BG);

    // Visibility styling (stroke patterns to differentiate access levels)
    styles
        .addElementStyle(ArchgenTags.TAG_VISIBILITY_PUBLIC)
        .stroke(COLOR_VISIBILITY_PUBLIC)
        .strokeWidth(2);
    styles
        .addElementStyle(ArchgenTags.TAG_VISIBILITY_PROTECTED)
        .stroke(COLOR_VISIBILITY_PROTECTED)
        .strokeWidth(2);
    styles
        .addElementStyle(ArchgenTags.TAG_VISIBILITY_PRIVATE)
        .stroke(COLOR_VISIBILITY_PRIVATE)
        .strokeWidth(1);
    styles
        .addElementStyle(ArchgenTags.TAG_VISIBILITY_PACKAGE)
        .stroke(COLOR_VISIBILITY_PACKAGE)
        .strokeWidth(1);

    // Java type styling (stroke width only, no color - differentiated by width)
    styles.addElementStyle(ArchgenTags.TAG_JAVA_INTERFACE).strokeWidth(3);
    styles.addElementStyle(ArchgenTags.TAG_JAVA_ENUM).strokeWidth(2);
    styles.addElementStyle(ArchgenTags.TAG_JAVA_ANNOTATION).strokeWidth(2);
    styles.addElementStyle(ArchgenTags.TAG_JAVA_ABSTRACT).strokeWidth(2);
    styles.addElementStyle(ArchgenTags.TAG_JAVA_CLASS).strokeWidth(1);

    // Dynamic package colors (background color per package)
    for (Map.Entry<String, String> entry : packageColorPalette.getPackageColorMap().entrySet()) {
      String packageTag = entry.getKey();
      String color = entry.getValue();
      styles
          .addElementStyle(packageTag)
          .background(color)
          .color(COLOR_MODULE_TEXT)
          .stroke(darkenColor(color));
    }
  }

  public static void configureStyles(Styles styles) {
    configureStyles(styles, new PackageColorPalette());
  }

  private static String darkenColor(String hexColor) {
    // Simple darkening: reduce each component by 20%
    String hex = hexColor.substring(1);
    int r = (int) (Integer.parseInt(hex.substring(0, 2), 16) * 0.8);
    int g = (int) (Integer.parseInt(hex.substring(2, 4), 16) * 0.8);
    int b = (int) (Integer.parseInt(hex.substring(4, 6), 16) * 0.8);
    return String.format("#%02X%02X%02X", r, g, b);
  }
}
