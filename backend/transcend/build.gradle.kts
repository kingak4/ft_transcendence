plugins {
   application
   pmd
   jacoco
   alias(libs.plugins.spring.boot)
   alias(libs.plugins.spring.management)
   alias(libs.plugins.formatter)
}

group = "code"
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
   mavenCentral()
   maven { url = uri("https://repo.spring.io/release") }
   maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
   implementation(libs.spring.modulith)
   implementation(libs.spring.web)
   implementation(libs.spring.validation)
   implementation(libs.spring.openapi)
   implementation(libs.mapstruct)
   implementation(libs.spring.security)
   implementation(libs.jjwt.api)
   runtimeOnly(libs.jjwt.impl)
   runtimeOnly(libs.jjwt.jackson)

   compileOnly(libs.lombok)
   annotationProcessor(libs.lombok)
   annotationProcessor(libs.bundles.mapstruct.annotation)
   testImplementation(libs.lombok)
   testAnnotationProcessor(libs.lombok)
   testAnnotationProcessor(libs.bundles.mapstruct.annotation)

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

   pmd {
      toolVersion = "7.4.0"
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
            xml.required = false
            csv.required = false
            html.outputLocation = layout.buildDirectory.dir("reports/jacoco")
         }
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
         reportPath = layout.buildDirectory.file("reports/checkstyle/main.html").get().asFile
         println("CheckstyleMain report: file://${reportPath.toURI().path}")
         reportPath = layout.buildDirectory.file("reports/checkstyle/test.html").get().asFile
         println("CheckstyleTest report: file://${reportPath.toURI().path}")
         reportPath = layout.buildDirectory.file("reports/pmd/main.html").get().asFile
         println("PmdMain report: file://${reportPath.toURI().path}")
         reportPath = layout.buildDirectory.file("reports/pmd/test.html").get().asFile
         println("PmdTest report: file://${reportPath.toURI().path}")

      }
   }

   javadoc {
      setDestinationDir(file(layout.buildDirectory.dir("reports/javadoc")))
      options.encoding = "UTF-8"
   }
}