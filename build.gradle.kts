plugins {
    kotlin("jvm") version "2.1.20" apply false
    id("org.sonarqube") version "6.2.0.5505"
}

group = "io.github.mpecan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sonar {
    properties {
        property("sonar.projectKey", "mpecan_jacoco-coverage-inspector")
        property("sonar.organization", "mpecan")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}