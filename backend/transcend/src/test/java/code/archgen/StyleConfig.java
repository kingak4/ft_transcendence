package code.archgen;

import com.structurizr.view.Styles;

public class StyleConfig {

  public static void configureStyles(Styles styles) {

    styles.addElementStyle("Open").background("#A9DCDF").color("#000000").stroke("#005f6b");

    styles.addElementStyle("Closed").background("#E0E0E0").color("#333333").stroke("#999999");

    styles.addElementStyle("Group").color("#444444").background("#f9f9f9");
  }
}
