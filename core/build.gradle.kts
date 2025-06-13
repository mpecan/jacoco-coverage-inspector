plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization")
    jacoco
    `maven-publish`
}

group = "io.github.mpecan"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${rootProject.extra["kotlinxSerializationVersion"]}")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.extra["junitVersion"]}")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testImplementation(gradleTestKit())
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    useJUnitPlatform()
}


kotlin {
    jvmToolchain(17)
}

jacoco {
    toolVersion = rootProject.extra["jacocoVersion"] as String
}

tasks.jacocoTestReport {
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
                "*.Companion"
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

publishing {
    repositories {
        mavenLocal() // For CI local publishing
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mpecan/jacoco-coverage-inspector")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            pom {
                name.set("JaCoCo Coverage Inspector Core")
                description.set("Core functionality for parsing and analyzing JaCoCo coverage reports")
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
                        email.set("mpecan@users.noreply.github.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/mpecan/jacoco-coverage-inspector.git")
                    developerConnection.set("scm:git:ssh://github.com/mpecan/jacoco-coverage-inspector.git")
                    url.set("https://github.com/mpecan/jacoco-coverage-inspector")
                }
            }
        }
    }
}