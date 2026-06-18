import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
   application
   pmd
   jacoco
   alias(libs.plugins.spring.boot)
   alias(libs.plugins.spring.management)
   alias(libs.plugins.formatter)
   alias(libs.plugins.asciidoctor)
   kotlin("jvm") version "1.9.23"
   kotlin("plugin.spring") version "1.9.23"
}

group = "code"
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
   mavenCentral()
//   maven { url = uri("https://repo.spring.io/release") }
   maven { url = uri("https://repo.spring.io/milestone") }
}

val asciidoctorExt by configurations.creating

dependencies {
   asciidoctorExt(libs.asciidoctorj.diagram)
   implementation(libs.commons.lang3)
   testImplementation(libs.bundles.testcontainers)
   implementation(libs.spring.modulith)
   implementation(libs.spring.web)
   implementation(libs.spring.actuator)
   implementation(libs.spring.sockets)
   implementation(libs.spring.validation)
   implementation(libs.spring.openapi)
   implementation(libs.bundles.springwolf) { exclude(group = "org.springframework.boot") }
   implementation(libs.spring.security)
   implementation(libs.passay)
   implementation(libs.spring.data.jpa)
   implementation(libs.jjwt.api)
   implementation(libs.liquibase)
   implementation(libs.dotenv.java)
   implementation(libs.bundles.cache)
   implementation(libs.bundles.structurizr)
   implementation(libs.embedded.redis)

   runtimeOnly(libs.postgres)
   runtimeOnly(libs.jjwt.impl)
   runtimeOnly(libs.jjwt.jackson)

   testImplementation(libs.spring.testcontainers)
   testImplementation(libs.jupiter.testcontainers)

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
   testImplementation(platform("io.kotest:kotest-bom:5.9.1"))
   testImplementation("io.kotest:kotest-runner-junit5")
   testImplementation("io.kotest:kotest-assertions-core")
   testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
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
      dependsOn("asciidoctorModulith")
      dependsOn("asciidoctor")
      dependsOn("generateStructurizr")
      doLast {
         val reportPath = layout.buildDirectory.file("reports/index.html").get().asFile
         println("Report index: file://${reportPath.toURI().path}")
      }
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
            "structurizr-docs" to layout.buildDirectory.dir("tmp/structurizr").get().asFile.absolutePath,
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
      setDestinationDir(file(layout.buildDirectory.dir("reports/javadoc")))
   }

   asciidoctor {
      dependsOn("javadoc");
      setSourceDir(layout.projectDirectory.dir("src/docs/asciidoc"))
      setOutputDir(layout.buildDirectory.dir("reports"))
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

      jvmArgs(
         "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
         "--add-opens", "java.base/java.io=ALL-UNNAMED"
      )
   }

   register<JavaExec>("generateStructurizr") {
      description = "Generate structurizr/workspace.json by scanning application classes"
      classpath = sourceSets.main.get().runtimeClasspath
      mainClass.set("code.StructurizrGenerator")
      args = listOf("code")
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
}