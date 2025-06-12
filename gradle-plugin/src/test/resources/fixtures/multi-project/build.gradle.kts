plugins {
    kotlin("jvm") version "2.1.20"
    jacoco
    id("io.github.mpecan.jacoco-inspector") apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "jacoco")
    apply(plugin = "io.github.mpecan.jacoco-inspector")
    
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
}