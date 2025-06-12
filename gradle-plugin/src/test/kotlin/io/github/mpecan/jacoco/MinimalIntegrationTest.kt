package io.github.mpecan.jacoco

import io.github.mpecan.jacoco.PluginFixtureTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Minimal integration tests for scenarios that can only be tested with real Gradle builds.
 * Most functionality is covered by unit tests.
 */
class MinimalIntegrationTest : PluginFixtureTest() {
    
    @Test
    fun `command line options are parsed correctly`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        // Test various command line options
        val result = runGradle(
            "listFileCoverage",
            "--format=json",
            "--minCoverage=50",
            "--coverageType=LINE",
            "--packageFilter=com.test"
        )
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        assertTrue(result.output.contains("\"className\""), "Should produce JSON output")
    }
    
    @Test
    fun `handles missing jacoco report gracefully`() {
        loadFixture("simple-project")
        
        // Don't generate report, just run the task
        val result = runGradle("listProjectCoverage")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        assertTrue(result.output.isNotEmpty(), "Should handle missing report gracefully")
    }
    
    @Test
    fun `all output formats work correctly`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        // Test each format
        listOf("table", "json", "csv", "markdown").forEach { format ->
            val result = runGradle("listProjectCoverage", "--format=$format")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome, 
                "Format $format should work")
        }
    }
}