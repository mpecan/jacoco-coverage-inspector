plugins {
    kotlin("jvm") version "2.2.10" apply false
    kotlin("plugin.serialization") version "2.2.10" apply false
    id("org.sonarqube") version "6.3.1.5724"
    id("io.github.gmazzo.gradle.testkit.jacoco") version "1.0.4" apply false
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
        
        // Include both Gradle and Maven coverage reports
        property("sonar.coverage.jacoco.xmlReportPaths", 
            "core/build/reports/jacoco/test/jacocoTestReport.xml," +
            "gradle-plugin/build/reports/jacoco/test/jacocoTestReport.xml," +
            "maven-plugin/target/site/jacoco/jacoco.xml")
        
        // Include compiled classes from both build systems
        property("sonar.java.binaries", 
            "gradle-plugin/build/classes," +
            "core/build/classes," +
            "maven-plugin/target/classes")
        
        // Configure modules to avoid duplicate indexing
        property("sonar.modules", "core,gradle-plugin,maven-plugin")
        
        // Core module configuration
        property("core.sonar.sources", "src/main")
        property("core.sonar.tests", "src/test")
        property("core.sonar.projectBaseDir", "core")
        
        // Gradle plugin module configuration  
        property("gradle-plugin.sonar.sources", "src/main")
        property("gradle-plugin.sonar.tests", "src/test")
        property("gradle-plugin.sonar.projectBaseDir", "gradle-plugin")
        
        // Maven plugin module configuration
        property("maven-plugin.sonar.sources", "src/main")
        property("maven-plugin.sonar.tests", "src/test")
        property("maven-plugin.sonar.projectBaseDir", "maven-plugin")
        property("maven-plugin.sonar.language", "kotlin")
    }
}