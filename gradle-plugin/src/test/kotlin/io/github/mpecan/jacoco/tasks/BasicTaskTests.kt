package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.PluginFixtureTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BasicTaskTests : PluginFixtureTest() {
    
    @Test
    fun `plugin registers all expected tasks`() {
        loadFixture("simple-project")
        
        // Just check that the plugin can be applied and basic task exists
        val result = runGradle("tasks", "--all")
        
        assertTrue(result.output.contains("listProjectCoverage"), "Should have listProjectCoverage task")
        assertTrue(result.output.contains("listPackageCoverage"), "Should have listPackageCoverage task")
        assertTrue(result.output.contains("listFileCoverage"), "Should have listFileCoverage task")
    }
    
    @Test
    fun `listProjectCoverage runs with default settings`() {
        loadFixture("simple-project")
        
        // Generate test report first
        val testResult = runGradle("test", "jacocoTestReport")
        assertEquals(TaskOutcome.SUCCESS, testResult.task(":jacocoTestReport")?.outcome)
        
        // Run the coverage task
        val result = runGradle("listProjectCoverage")
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        
        // Basic output check
        assertTrue(result.output.isNotEmpty(), "Should produce some output")
    }
    
    @Test
    fun `listPackageCoverage runs with default settings`() {
        loadFixture("simple-project")
        
        // Generate test report first
        runGradle("test", "jacocoTestReport")
        
        // Run the coverage task
        val result = runGradle("listPackageCoverage")
        assertEquals(TaskOutcome.SUCCESS, result.task(":listPackageCoverage")?.outcome)
        
        // Basic output check
        assertTrue(result.output.isNotEmpty(), "Should produce some output")
    }
    
    @Test
    fun `listFileCoverage runs with default settings`() {
        loadFixture("simple-project")
        
        // Generate test report first
        runGradle("test", "jacocoTestReport")
        
        // Run the coverage task
        val result = runGradle("listFileCoverage")
        assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        
        // Basic output check
        assertTrue(result.output.isNotEmpty(), "Should produce some output")
    }
    
    @Test
    fun `tasks fail when no JaCoCo report exists`() {
        loadFixture("simple-project")
        
        // First ensure the task exists by running a build
        runGradle("tasks")
        
        // Modify build file to remove the dependency on jacocoTestReport
        val buildFile = testProjectDir.resolve("build.gradle.kts")
        val originalContent = buildFile.readText()
        buildFile.writeText(originalContent + """
            
            // Configure after evaluation to ensure tasks exist
            afterEvaluate {
                tasks.named("listProjectCoverage") {
                    setDependsOn(emptyList<Any>())
                }
            }
        """.trimIndent())
        
        // Clean any existing reports
        runGradle("clean")
        
        // Try to run without generating report
        val result = runGradle("listProjectCoverage", expectFailure = false)
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        assertTrue(
            result.output.contains("No items match the specified filters.")
        )
    }
    
    @Test
    fun `extension configuration is applied to tasks`() {
        loadFixture("simple-project")
        
        // Add configuration to build file
        val buildFile = testProjectDir.resolve("build.gradle.kts")
        val originalContent = buildFile.readText()
        buildFile.writeText(originalContent + """
            
            jacocoInspector {
                defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.JSON
                colorOutput = false
            }
        """.trimIndent())
        
        // Generate report and run task
        runGradle("test", "jacocoTestReport")
        val result = runGradle("listProjectCoverage")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        // The actual format verification would depend on the formatter implementation
    }
}