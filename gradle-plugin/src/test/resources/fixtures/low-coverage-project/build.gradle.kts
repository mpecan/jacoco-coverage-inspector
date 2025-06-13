plugins {
    kotlin("jvm") version "2.1.20"
    jacoco
    id("io.github.mpecan.jacoco-inspector")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.assertj:assertj-core:3.25.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}