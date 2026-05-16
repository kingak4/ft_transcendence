package code.archgen.model;

/** Extracts package-based tags for styling (e.g., domain, persistence, endpoints). */
public class PackageTagExtractor {

  /**
   * Extracts the first-level subpackage from a fully qualified class name and module base package.
   * For example: if basePackage="code.users" and classFqcn="code.users.domain.User", returns
   * "Package.domain".
   */
  public static String extractPackageTag(String basePackageName, String classFqcn) {
    String pkgName = extractPackage(classFqcn);

    if (!pkgName.startsWith(basePackageName + ".")) {
      return null;
    }

    String relativePkg = pkgName.substring(basePackageName.length() + 1);
    int firstDot = relativePkg.indexOf('.');

    String subPackage = (firstDot == -1) ? relativePkg : relativePkg.substring(0, firstDot);
    return ArchgenTags.TAG_PACKAGE_PREFIX + subPackage;
  }

  private static String extractPackage(String classFqcn) {
    int lastDot = classFqcn.lastIndexOf('.');
    return (lastDot == -1) ? "" : classFqcn.substring(0, lastDot);
  }
}
