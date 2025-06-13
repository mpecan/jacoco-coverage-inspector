package io.github.mpecan.jacoco

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MultiProjectFixtureTest: PluginFixtureTest() {
    
    @Test
    fun `plugin works with multi-project build`() {
        loadFixture("multi-project")
        
        // Run tests and generate reports for all projects
        val result = runGradle("test", "jacocoTestReport", "listProjectCoverage")
        assertEquals(TaskOutcome.SUCCESS, result.task(":module-a:listProjectCoverage")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, result.task(":module-b:listProjectCoverage")?.outcome)
        
        // Verify that coverage is shown for each module
        assertTrue(result.output.contains("module-a") || result.output.contains("ServiceA"))
        assertTrue(result.output.contains("module-b") || result.output.contains("ServiceB"))
    }
    
    @Test
    fun `listProjectCoverage works in multi-project with different formats`() {
        loadFixture("multi-project")
        runGradle("test", "jacocoTestReport")
        
        // Test JSON format in multi-project
        val jsonResult = runGradle("listProjectCoverage", "--format=json")
        assertEquals(TaskOutcome.SUCCESS, jsonResult.task(":module-a:listProjectCoverage")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, jsonResult.task(":module-b:listProjectCoverage")?.outcome)
        assertTrue(jsonResult.output.contains("\"projectName\""))
        
        // Test CSV format in multi-project
        val csvResult = runGradle("listProjectCoverage", "--format=csv")
        assertEquals(TaskOutcome.SUCCESS, csvResult.task(":module-a:listProjectCoverage")?.outcome)
        assertEquals(TaskOutcome.SUCCESS, csvResult.task(":module-b:listProjectCoverage")?.outcome)
        assertTrue(csvResult.output.contains("PROJECT,"))
    }
    
    @Test
    fun `subproject tasks work independently`() {
        loadFixture("multi-project")
        runGradle("test", "jacocoTestReport")
        
        // Test module-a coverage
        val moduleAResult = runGradle(":module-a:listFileCoverage")
        assertEquals(TaskOutcome.SUCCESS, moduleAResult.task(":module-a:listFileCoverage")?.outcome)
        
        // Test module-b coverage
        val moduleBResult = runGradle(":module-b:listFileCoverage")
        assertEquals(TaskOutcome.SUCCESS, moduleBResult.task(":module-b:listFileCoverage")?.outcome)
    }
}