plugins {
    id("java")
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    `signing`
    id("com.gradle.plugin-publish") version "1.3.1"
    jacoco
    id("io.github.gmazzo.gradle.testkit.jacoco")
}

group = "io.github.mpecan"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":core"))
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junitVersion"]}")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testImplementation(gradleTestKit())
    testImplementation("org.assertj:assertj-core:${rootProject.extra["assertjVersion"]}")
    testImplementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = rootProject.extra["jacocoVersion"] as String
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
    website = "https://github.com/mpecan/jacoco-coverage-inspector"
    vcsUrl = "https://github.com/mpecan/jacoco-coverage-inspector"
    
    plugins {
        create("jacocoCoverageInspector") {
            id = "io.github.mpecan.jacoco-inspector"
            implementationClass = "io.github.mpecan.jacoco.JacocoCoverageInspectorPlugin"
            displayName = "JaCoCo Coverage Inspector"
            description = "Inspect and parse JaCoCo coverage reports with human and machine-readable outputs"
            tags = listOf("jacoco", "coverage", "testing", "reporting")
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
                url.set("https://github.com/mpecan/jacoco-coverage-inspector")

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
                    connection.set("scm:git:git://github.com/mpecan/jacoco-coverage-inspector.git")
                    developerConnection.set("scm:git:ssh://github.com:mpecan/jacoco-coverage-inspector.git")
                    url.set("https://github.com/mpecan/jacoco-coverage-inspector")
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}