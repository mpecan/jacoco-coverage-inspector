package io.github.mpecan.jacoco

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleFixtureTest: PluginFixtureTest() {
    @Test
    fun `plugin works with basic project`() {
        loadFixture("simple-project")

        val result = runGradle("test", "jacocoTestReport", "listProjectCoverage", "--stacktrace")

        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
    }
}