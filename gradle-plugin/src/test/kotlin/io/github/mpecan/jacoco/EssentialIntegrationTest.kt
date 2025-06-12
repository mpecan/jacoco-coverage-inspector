package io.github.mpecan.jacoco

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Essential integration tests that verify end-to-end functionality.
 * Most functionality is now covered by unit tests.
 */
class EssentialIntegrationTest : PluginFixtureTest() {
    
    @Test
    fun `plugin works end-to-end with simple project`() {
        loadFixture("simple-project")
        
        val result = runGradle("test", "jacocoTestReport", "listProjectCoverage", "--format=json")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":test")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":jacocoTestReport")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        
        // Verify JSON output format
        assertTrue(result.output.contains("\"projectName\""), "Should produce JSON output")
    }
    
    @Test
    fun `plugin works with multi-project builds`() {
        loadFixture("multi-project")
        
        val result = runGradle("test", "jacocoTestReport", ":module-a:listFileCoverage", "--format=table")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":module-a:listFileCoverage")?.outcome)
        assertTrue(result.output.isNotEmpty(), "Should produce table output")
    }
    
    @Test
    fun `extension configuration overrides defaults`() {
        loadFixture("simple-project")
        
        // Modify build file to configure extension
        val buildFile = testProjectDir.resolve("build.gradle.kts")
        val originalContent = buildFile.readText()
        buildFile.writeText(
            originalContent + """
            
            jacocoInspector {
                defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.JSON
                colorOutput = false
                minLineCoverage = 0.0  // Show all files
            }
        """.trimIndent()
        )
        
        runGradle("test", "jacocoTestReport")
        val result = runGradle("listFileCoverage")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        // Should use JSON format from extension
        assertTrue(result.output.contains("\"className\""), "Should use JSON format from extension")
    }
    
    @Test
    fun `command line options override extension configuration`() {
        loadFixture("simple-project")
        
        // Configure extension with JSON format
        val buildFile = testProjectDir.resolve("build.gradle.kts")
        val originalContent = buildFile.readText()
        buildFile.writeText(
            originalContent + """
            
            jacocoInspector {
                defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.JSON
            }
        """.trimIndent()
        )
        
        runGradle("test", "jacocoTestReport")
        // Override with CSV format via command line
        val result = runGradle("listProjectCoverage", "--format=csv")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        // Should not contain JSON output
        assertTrue(!result.output.contains("\"projectName\""), "Should not use JSON format")
    }
    
    @Test
    fun `tasks handle missing jacoco report gracefully`() {
        loadFixture("simple-project")
        
        // Clean any existing reports
        runGradle("clean")
        
        // Try to run without generating report (this should not fail)
        val result = runGradle("listProjectCoverage")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        assertTrue(result.output.isNotEmpty(), "Should produce some output even without report")
    }
}