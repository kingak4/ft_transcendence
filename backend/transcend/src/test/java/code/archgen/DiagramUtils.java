package code.archgen;

class DiagramUtils {
  public static String sanitize(String input) {
    return input.replaceAll("[^a-zA-Z0-9\\-_]", "-");
  }
}
