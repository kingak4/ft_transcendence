import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
   application
   pmd
   jacoco
   alias(libs.plugins.spring.boot)
   alias(libs.plugins.spring.management)
   alias(libs.plugins.formatter)
   alias(libs.plugins.asciidoctor)
}

group = "code"
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
   mavenCentral()
   maven { url = uri("https://repo.spring.io/release") }
   maven { url = uri("https://repo.spring.io/milestone") }
}

val asciidoctorExt by configurations.creating

dependencies {
   asciidoctorExt(libs.asciidoctorj.diagram)

   implementation(libs.spring.modulith)
   implementation(libs.spring.web)
   implementation(libs.spring.validation)
   implementation(libs.spring.openapi)
   implementation(libs.spring.security)
   implementation(libs.passay)
   implementation(libs.spring.data.jpa)
   implementation(libs.jjwt.api)
   implementation(libs.liquibase)
   implementation(libs.dotenv.java)

   runtimeOnly(libs.postgres)
   runtimeOnly(libs.jjwt.impl)
   runtimeOnly(libs.jjwt.jackson)
   runtimeOnly(libs.h2)

   compileOnly(libs.lombok)
   annotationProcessor(libs.lombok)
   testImplementation(libs.lombok)
   testAnnotationProcessor(libs.lombok)
   annotationProcessor(libs.bundles.mapstruct.annotation)
   testAnnotationProcessor(libs.bundles.mapstruct.annotation)
   implementation(libs.mapstruct)

   testImplementation(libs.junit.jupiter)
   testRuntimeOnly(libs.junit.platform)
   testImplementation(libs.bundles.spring.test)
}

java {
   toolchain {
      languageVersion.set(JavaLanguageVersion.of(21))
   }
}

tasks {
   compileJava {
      options.encoding = "UTF-8"
      options.compilerArgs.addAll(listOf(
         "-parameters",
         "-Amapstruct.defaultComponentModel=spring",
         "-Amapstruct.unmappedTargetPolicy=ERROR",
         "-Amapstruct.suppressGeneratorTimestamp=true"
      ))
   }
   compileTestJava {
      options.encoding = "UTF-8"
   }
   bootJar {
      archiveFileName = "${project.name}-${version}.${archiveExtension.get()}"
   }
   jar {
      enabled = false
   }

   register("docs") {
      dependsOn("check")
      dependsOn("asciidoctorClassUtil")
      dependsOn("asciidoctorModulith")
      dependsOn("asciidoctor");
   }

   withType<AsciidoctorTask>().configureEach {
      baseDirFollowsSourceFile()
      useIntermediateWorkDir()
      configurations("asciidoctorExt")
      sources {
         include("index.adoc")
      }
      asciidoctorj {
         modules {
            diagram.use()
         }
         setFatalWarnings(listOf(org.asciidoctor.log.Severity.ERROR))
         attributes(mapOf(
            "toc" to "left",
            "icons" to "font",
            "projectdir" to projectDir.absolutePath,
            "imagesdir" to "images",
            "modulith-docs" to layout.buildDirectory.dir("tmp/modulith").get().asFile.absolutePath,
            "classUtil-docs" to layout.buildDirectory.dir("tmp/classUtil").get().asFile.absolutePath,
            "plantumlconfig" to "${projectDir.absolutePath}/src/docs/asciidoc/plantuml.cfg"
         ))
      }
      jvm {
         jvmArgs(
            "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
            "--add-opens", "java.base/java.io=ALL-UNNAMED"
         )
      }
   }

   javadoc {
      options.encoding = "UTF-8"
      (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
      isFailOnError = false
   }

   asciidoctor {
      dependsOn("javadoc");
      setSourceDir(layout.projectDirectory.dir("src/docs/asciidoc"))
      setOutputDir(layout.buildDirectory.dir("reports"))
   }

   register<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctorClassUtil") {
      description = "Generate AsciiDoc for ClassUtil class diagrams"
      dependsOn(test)

      setSourceDir(layout.projectDirectory.dir("src/docs/asciidoc/classUtil"))
      setOutputDir(layout.buildDirectory.dir("reports/classUtil"))
      setBaseDir(layout.buildDirectory.dir("tmp/classUtil").get().asFile)
   }

   register<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctorModulith") {
      description = "Generate AsciiDoc for modulith diagrams"
      dependsOn(test)

      setSourceDir(layout.projectDirectory.dir("src/docs/asciidoc/modulith"))
      setOutputDir(layout.buildDirectory.dir("reports/modulith"))
      setBaseDir(layout.buildDirectory.dir("tmp/modulith").get().asFile)
   }

   test {
      useJUnitPlatform()
      testLogging {
         events("passed", "skipped", "failed")
      }
   }

   check {
      dependsOn(spotlessApply)
      dependsOn(spotlessCheck)
      dependsOn(pmdMain)
      dependsOn(pmdTest)
      dependsOn(jacocoTestReport)
   }

   spotless {
      java {
         target("src/**/*.java")
         googleJavaFormat()
         removeUnusedImports()
      }
   }

   pmd {
      toolVersion = libs.versions.pmd.get()
      isConsoleOutput = false
      isIgnoreFailures = true
      rulesMinimumPriority = 5
      ruleSets = listOf("category/java/errorprone.xml", "category/java/bestpractices.xml")
      pmdMain {
      }
      pmdTest {
      }
   }

   jacoco {
      jacocoTestReport {
         reports {
            xml.required = true
            csv.required = true
            html.outputLocation = layout.buildDirectory.dir("reports/jacoco")
         }
         classDirectories.setFrom(
            files(classDirectories.files.map {
               fileTree(it) {
                  exclude("**/*Mapper.class")
                  exclude("**/*MapperImpl.class")
                  exclude("**/config/**")
                  exclude("**/*Exception.class")
                  exclude("**/*ExceptionHandler.class")
                  exclude("**/TranscendApp.class")
                  exclude("**/HelloRestController.class")
                  exclude("**/ValidProfileInitializer.class")
               }
            })
         )
         doLast {
            val reportPath = layout.buildDirectory.file("reports/jacoco/index.html").get().asFile
            println("Jacoco report: file://${reportPath.toURI().path}")
         }

         dependsOn(test)
      }
   }

   register("report") {
      description = "Reports paths to Html documentation files"
      doLast {
         var reportPath = layout.buildDirectory.file("reports/jacoco/index.html").get().asFile
         println("Jacoco report: file://${reportPath.toURI().path}")
         reportPath = layout.buildDirectory.file("reports/pmd/main.html").get().asFile
         println("PmdMain report: file://${reportPath.toURI().path}")
         reportPath = layout.buildDirectory.file("reports/pmd/test.html").get().asFile
         println("PmdTest report: file://${reportPath.toURI().path}")
         reportPath = layout.buildDirectory.file("docs/asciidoc/index.html").get().asFile
         println("Documentation: file://${reportPath.toURI().path}")
      }
   }

   javadoc {
      setDestinationDir(file(layout.buildDirectory.dir("reports/javadoc")))
      options.encoding = "UTF-8"
   }
}