plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "jacoco-coverage-inspector"
include("gradle-plugin")
include("core")
// maven-plugin is a separate Maven project and should not be included in Gradle build