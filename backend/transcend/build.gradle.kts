plugins {
   application
   pmd
   jacoco
   alias(libs.plugins.spring.boot)
   alias(libs.plugins.spring.management)
   alias(libs.plugins.formatter)
   id("org.asciidoctor.jvm.convert") version "4.0.2"
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
   asciidoctorExt("org.asciidoctor:asciidoctorj-diagram:2.2.14")
   
   implementation(libs.spring.modulith)
   implementation(libs.spring.web)
   implementation(libs.spring.validation)
   implementation(libs.spring.openapi)
   implementation(libs.spring.security)
   implementation(libs.passay)
   implementation(libs.jjwt.api)
   runtimeOnly(libs.jjwt.impl)
   runtimeOnly(libs.jjwt.jackson)

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

   javadoc {
      options.encoding = "UTF-8"
      (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
      isFailOnError = false
   }

   asciidoctor {
      setSourceDir(layout.projectDirectory.dir("src/docs/asciidoc"))
      setOutputDir(layout.buildDirectory.dir("docs/asciidoc"))

      resources {
         from(layout.buildDirectory.dir("spring-modulith-docs"))
      }

      useIntermediateWorkDir()

      baseDirFollowsSourceFile()
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
            "imagesdir" to "images"
         ))
      }
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
      doLast {
         var reportPath = layout.buildDirectory.file("reports/jacoco/index.html").get().asFile
         println("Jacoco report: file://${reportPath.toURI().path}")
         reportPath = layout.buildDirectory.file("reports/pmd/main.html").get().asFile
         println("PmdMain report: file://${reportPath.toURI().path}")
         reportPath = layout.buildDirectory.file("reports/pmd/test.html").get().asFile
         println("PmdTest report: file://${reportPath.toURI().path}")

      }
   }
}