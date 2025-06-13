plugins {
    id("java")
    kotlin("jvm")
    kotlin("plugin.serialization")
    jacoco
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
    jvmToolchain(21)
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