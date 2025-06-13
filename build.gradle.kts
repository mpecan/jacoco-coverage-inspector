plugins {
    kotlin("jvm") version "2.1.20" apply false
    kotlin("plugin.serialization") version "2.1.20" apply false
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
    }
}