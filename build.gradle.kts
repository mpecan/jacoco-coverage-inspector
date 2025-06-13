plugins {
    kotlin("jvm") version "2.1.21" apply false
    kotlin("plugin.serialization") version "2.1.21" apply false
    id("org.sonarqube") version "6.2.0.5505"
    id("io.github.gmazzo.gradle.testkit.jacoco") version "1.0.3" apply false
}

group = "io.github.mpecan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Define version catalog for all subprojects
subprojects {
    repositories {
        mavenCentral()
    }
    
    // Make versions available to subprojects
    extra["kotlinVersion"] = property("kotlinVersion")
    extra["junitVersion"] = property("junitVersion")
    extra["junitPlatformVersion"] = property("junitPlatformVersion")
    extra["jacocoVersion"] = property("jacocoVersion")
    extra["gradlePluginPublishVersion"] = property("gradlePluginPublishVersion")
    extra["kotlinxSerializationVersion"] = property("kotlinxSerializationVersion")
    extra["mockkVersion"] = property("mockkVersion")
    extra["assertjVersion"] = property("assertjVersion")
    extra["testKitJacocoVersion"] = property("testKitJacocoVersion")
}

sonar {
    properties {
        property("sonar.projectKey", "mpecan_jacoco-coverage-inspector")
        property("sonar.organization", "mpecan")
        property("sonar.host.url", "https://sonarcloud.io")
        
        // Include Maven plugin source code and coverage
        property("sonar.sources", "gradle-plugin/src/main,core/src/main,maven-plugin/src/main")
        property("sonar.tests", "gradle-plugin/src/test,core/src/test,maven-plugin/src/test")
        
        // Include both Gradle and Maven coverage reports
        property("sonar.coverage.jacoco.xmlReportPaths", 
            "build/reports/jacoco/test/jacocoTestReport.xml," +
            "core/build/reports/jacoco/test/jacocoTestReport.xml," +
            "gradle-plugin/build/reports/jacoco/test/jacocoTestReport.xml," +
            "maven-plugin/target/site/jacoco/jacoco.xml")
        
        // Include compiled classes from both build systems
        property("sonar.java.binaries", 
            "gradle-plugin/build/classes," +
            "core/build/classes," +
            "maven-plugin/target/classes")
        
        // Specify language for Maven plugin Kotlin files
        property("sonar.kotlin.file.suffixes", ".kt")
    }
}