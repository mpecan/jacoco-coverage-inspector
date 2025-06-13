plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
rootProject.name = "jacoco-coverage-inspector"
include("gradle-plugin")
include("core")
// maven-plugin is a separate Maven project and should not be included in Gradle build