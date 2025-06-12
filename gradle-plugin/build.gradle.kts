plugins {
    id("java")
    kotlin("jvm") version "2.1.20"
    `java-gradle-plugin`
    `maven-publish`
    jacoco
    id("io.github.gmazzo.gradle.testkit.jacoco") version "1.0.3"
}

group = "io.github.mpecan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        csv.required = true
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }

        rule {
            element = "CLASS"
            excludes = listOf(
                "*.Main*",
                "*.MainKt*",
                "*.Companion",
                "*\$\$serializer*",
                "*.*inlined*",
                "*.generateOutput.*inlined.*"
            )
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

kotlin {
    jvmToolchain(21)
}

gradlePlugin {
    plugins {
        create("jacocoCoverageInspector") {
            id = "io.github.mpecan.jacoco-inspector"
            implementationClass = "io.github.mpecan.jacoco.JacocoCoverageInspectorPlugin"
            displayName = "JaCoCo Coverage Inspector"
            description = "Inspect and parse JaCoCo coverage reports with human and machine-readable outputs"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("JaCoCo Coverage Inspector Gradle Plugin")
                description.set("Gradle plugin for inspecting and parsing JaCoCo coverage reports")
                url.set("https://github.com/mpecan/gradle-plugin-jacoco-agent")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("mpecan")
                        name.set("mpecan")
                        url.set("https://github.com/mpecan")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/mpecan/gradle-plugin-jacoco-agent.git")
                    developerConnection.set("scm:git:ssh://github.com:mpecan/gradle-plugin-jacoco-agent.git")
                    url.set("https://github.com/mpecan/gradle-plugin-jacoco-agent")
                }
            }
        }
    }
}