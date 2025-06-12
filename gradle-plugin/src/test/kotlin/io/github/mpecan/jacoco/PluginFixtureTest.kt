package io.github.mpecan.jacoco

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.io.File

abstract class PluginFixtureTest {
    @TempDir
    lateinit var testProjectDir: File

    protected fun loadFixture(fixtureName: String) {
        val fixtureDir = File("src/test/resources/fixtures/$fixtureName")
        fixtureDir.copyRecursively(testProjectDir, overwrite = true)
    }

    protected fun runGradle(vararg arguments: String, expectFailure: Boolean = false) = GradleRunner.create()
        .withProjectDir(testProjectDir)
        .withArguments(*arguments)
        .withPluginClasspath()
        .forwardOutput()  // Shows output in test console
        .run {
            if (expectFailure) buildAndFail() else build()
        }
}