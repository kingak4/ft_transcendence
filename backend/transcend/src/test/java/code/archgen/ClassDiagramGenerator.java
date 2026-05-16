package code.archgen;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.core.ApplicationModule;

@Slf4j
@RequiredArgsConstructor
public class ClassDiagramGenerator {

  private final List<ApplicationModule> modules;
  private final Set<String> projectBasePackages;

  public static ClassDiagramGenerator create(List<ApplicationModule> modules) {
    Set<String> basePackages =
        modules.stream().map(m -> m.getBasePackage().getName()).collect(Collectors.toSet());
    return new ClassDiagramGenerator(modules, basePackages);
  }

  public void generateAll(Path outputPath) throws IOException {
    Files.createDirectories(outputPath);

    generatePackageOverviewDiagram(outputPath);
    for (ApplicationModule module : modules) {
      generatePerFolderDiagrams(module, outputPath);
    }
  }

  private void generatePackageOverviewDiagram(Path outputPath) throws IOException {
    String[] packages =
        modules.stream().map(m -> m.getBasePackage().getName()).toArray(String[]::new);

    JavaClasses allContextClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(packages);

    Set<String> topLevelPackages = collectTopLevelPackages(allContextClasses);

    Set<String> relations = buildModuleDependencies(allContextClasses);
    String diagramText =
        PackageOverviewBuilder.build(
            projectBasePackages, allContextClasses, topLevelPackages, relations);
    Files.writeString(outputPath.resolve("packages-overview.puml"), diagramText);

    log.debug("Generated packages-overview.puml");
  }

  private Set<String> collectTopLevelPackages(JavaClasses allContextClasses) {
    Set<String> topLevel = new HashSet<>();
    for (var clazz : allContextClasses) {
      String pkg = clazz.getPackageName();
      String topLevelPkg = extractTopLevelPackage(pkg);
      if (topLevelPkg != null) {
        topLevel.add(topLevelPkg);
      }
    }
    return topLevel;
  }

  private String extractTopLevelPackage(String packageName) {
    for (String base : projectBasePackages) {
      if (packageName.equals(base)) {
        return base;
      }
      if (packageName.startsWith(base + ".")) {
        String rest = packageName.substring(base.length() + 1);
        if (!rest.contains(".")) {
          return base;
        }
      }
    }
    return null;
  }

  private Set<String> buildModuleDependencies(JavaClasses allContextClasses) {
    Set<String> relations = new HashSet<>();
    for (var clazz : allContextClasses) {
      String srcPkg = findOwningModulePackage(clazz.getPackageName());
      if (srcPkg == null) continue;
      for (var dep : clazz.getDirectDependenciesFromSelf()) {
        var target = dep.getTargetClass();
        String tgtPkg = findOwningModulePackage(target.getPackageName());
        if (tgtPkg == null) continue;
        if (!srcPkg.equals(tgtPkg)) {
          String srcAlias = RenderUtils.sanitizeFileName(srcPkg);
          String tgtAlias = RenderUtils.sanitizeFileName(tgtPkg);
          relations.add(String.format("%s --> %s", srcAlias, tgtAlias));
        }
      }
    }
    return relations;
  }

  private void generatePerFolderDiagrams(ApplicationModule module, Path outputPath) {
    JavaClasses moduleClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(module.getBasePackage().getName());

    Set<String> firstLevelFolders = extractFirstLevelFolders(moduleClasses, module);
    DiagramRenderer renderer = new DiagramRenderer();

    for (String folder : firstLevelFolders) {
      processFolderDiagram(module, folder, renderer, outputPath);
    }
  }

  private Set<String> extractFirstLevelFolders(
      JavaClasses moduleClasses, ApplicationModule module) {
    String base = module.getBasePackage().getName();
    Set<String> folders = new HashSet<>();

    for (var clazz : moduleClasses) {
      String pkg = clazz.getPackageName();

      if (pkg.startsWith(base + ".")) {
        String rest = pkg.substring(base.length() + 1);
        String firstSegment = rest.contains(".") ? rest.substring(0, rest.indexOf('.')) : rest;
        folders.add(base + "." + firstSegment);
      }
    }
    return folders;
  }

  private void processFolderDiagram(
      ApplicationModule module,
      String firstLevelFolder,
      DiagramRenderer renderer,
      Path outputPath) {
    try {
      Set<String> packagesToImport = collectFolderPackagesAndDependencies(firstLevelFolder, module);

      if (packagesToImport.isEmpty()) {
        log.warn("No packages found for first-level folder: {}", firstLevelFolder);
        return;
      }

      JavaClasses diagramClasses = importPackages(packagesToImport);
      if (diagramClasses == null) {
        return;
      }

      String fileName = buildFolderDiagramFileName(module, firstLevelFolder);
      renderer.renderAndWrite(diagramClasses, outputPath.resolve(fileName));
      log.debug("Generated {} for module {}", fileName, module.getIdentifier());
    } catch (IOException e) {
      log.error("Failed rendering diagram for {}: {}", firstLevelFolder, e.getMessage());
    }
  }

  private Set<String> collectFolderPackagesAndDependencies(
      String firstLevelFolder, ApplicationModule module) {
    Set<String> folderPackages = new HashSet<>();
    String basePackage = module.getBasePackage().getName();

    JavaClasses moduleClasses =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(basePackage);

    for (var clazz : moduleClasses) {
      String pkg = clazz.getPackageName();
      if (pkg.equals(firstLevelFolder) || pkg.startsWith(firstLevelFolder + ".")) {
        folderPackages.add(pkg);
      }
    }

    Set<String> directDeps = new HashSet<>();
    for (String pkg : folderPackages) {
      JavaClasses pkgClasses =
          new ClassFileImporter()
              .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
              .importPackages(pkg);

      for (var clazz : pkgClasses) {
        for (var dep : clazz.getDirectDependenciesFromSelf()) {
          var target = dep.getTargetClass();
          String targetPkg = target.getPackageName();
          if (isExternalToFolder(targetPkg, firstLevelFolder) && isInProject(targetPkg)) {
            directDeps.add(targetPkg);
          }
        }
      }
    }

    folderPackages.addAll(directDeps);
    return folderPackages;
  }

  private JavaClasses importPackages(Set<String> packageNames) {
    String[] pkgsArray = packageNames.toArray(String[]::new);
    try {
      return new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages(pkgsArray);
    } catch (OutOfMemoryError oom) {
      log.error("OOM while importing packages: {}", oom.getMessage());
      System.gc();
      return null;
    } catch (Exception e) {
      log.error("Failed importing packages: {}", e.getMessage());
      return null;
    }
  }

  private String buildFolderDiagramFileName(ApplicationModule module, String firstLevelFolder) {
    String base = module.getBasePackage().getName();
    String relative = RenderUtils.extractRelativeFolderName(firstLevelFolder, base);
    String safeFolderName = relative.isEmpty() ? "root" : RenderUtils.sanitizeFileName(relative);

    return "components-"
        + RenderUtils.sanitizeFileName(module.getIdentifier().toString())
        + "-"
        + safeFolderName
        + ".puml";
  }

  private boolean isExternalToFolder(String packageName, String firstLevelFolder) {
    return !packageName.equals(firstLevelFolder) && !packageName.startsWith(firstLevelFolder + ".");
  }

  private boolean isInProject(String packageName) {
    for (String basePkg : projectBasePackages) {
      if (packageName.equals(basePkg) || packageName.startsWith(basePkg + ".")) {
        return true;
      }
    }
    return false;
  }

  private String findOwningModulePackage(String packageName) {
    for (String base : projectBasePackages) {
      if (packageName.equals(base) || packageName.startsWith(base + ".")) {
        return base;
      }
    }
    return null;
  }
}
