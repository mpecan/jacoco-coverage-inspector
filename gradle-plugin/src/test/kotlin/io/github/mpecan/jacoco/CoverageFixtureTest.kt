package io.github.mpecan.jacoco

import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoverageFixtureTest: PluginFixtureTest() {
    
    @Test
    fun `listProjectCoverage works with different output formats`() {
        loadFixture("simple-project")
        
        // Test default table format
        val tableResult = runGradle("test", "jacocoTestReport", "listProjectCoverage")
        assertEquals(TaskOutcome.SUCCESS, tableResult.task(":listProjectCoverage")?.outcome)
        assertTrue(tableResult.output.contains("PROJECT COVERAGE SUMMARY"))
        
        // Test JSON format
        val jsonResult = runGradle("listProjectCoverage", "--format=json")
        assertEquals(TaskOutcome.SUCCESS, jsonResult.task(":listProjectCoverage")?.outcome)
        assertTrue(jsonResult.output.contains("\"projectName\""))
        
        // Test CSV format
        val csvResult = runGradle("listProjectCoverage", "--format=csv")
        assertEquals(TaskOutcome.SUCCESS, csvResult.task(":listProjectCoverage")?.outcome)
        assertTrue(csvResult.output.contains("PROJECT,"))
        
        // Test Markdown format
        val markdownResult = runGradle("listProjectCoverage", "--format=markdown")
        assertEquals(TaskOutcome.SUCCESS, markdownResult.task(":listProjectCoverage")?.outcome)
        assertTrue(markdownResult.output.contains("**Project:**"))
    }
    
    @Test
    fun `listFileCoverage works with filtering`() {
        loadFixture("simple-project")
        
        // Generate coverage first
        runGradle("test", "jacocoTestReport")
        
        // Test default file coverage
        val defaultResult = runGradle("listFileCoverage")
        assertEquals(TaskOutcome.SUCCESS, defaultResult.task(":listFileCoverage")?.outcome)
        
        // Test with minimum coverage filter
        val filteredResult = runGradle("listFileCoverage", "--minCoverage=50")
        assertEquals(TaskOutcome.SUCCESS, filteredResult.task(":listFileCoverage")?.outcome)
        
        // Test with coverage type filter
        val typeResult = runGradle("listFileCoverage", "--coverageType=INSTRUCTION")
        assertEquals(TaskOutcome.SUCCESS, typeResult.task(":listFileCoverage")?.outcome)
        
        // Test with package filter
        val packageResult = runGradle("listFileCoverage", "--packageFilter=com.test")
        assertEquals(TaskOutcome.SUCCESS, packageResult.task(":listFileCoverage")?.outcome)
    }
    
    @Test
    fun `listPackageCoverage works with different scenarios`() {
        loadFixture("simple-project")
        
        // Generate coverage first
        runGradle("test", "jacocoTestReport")
        
        // Test package coverage
        val result = runGradle("listPackageCoverage")
        assertEquals(TaskOutcome.SUCCESS, result.task(":listPackageCoverage")?.outcome)
        assertTrue(result.output.contains("PACKAGE COVERAGE"))
        
        // Test with JSON format
        val jsonResult = runGradle("listPackageCoverage", "--format=json")
        assertEquals(TaskOutcome.SUCCESS, jsonResult.task(":listPackageCoverage")?.outcome)
        assertTrue(jsonResult.output.contains("\"packageName\""))
    }
    
    @Test
    fun `plugin works with low coverage project`() {
        loadFixture("low-coverage-project")
        
        val result = runGradle("test", "jacocoTestReport", "listProjectCoverage")
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        
        // Verify that low coverage is reported
        val fileResult = runGradle("listFileCoverage", "--minCoverage=0")
        assertEquals(TaskOutcome.SUCCESS, fileResult.task(":listFileCoverage")?.outcome)
        assertTrue(fileResult.output.contains("Calculator"))
    }
    
    @Test
    fun `plugin works with zero coverage project`() {
        loadFixture("zero-coverage-project")
        
        val result = runGradle("test", "jacocoTestReport", "listProjectCoverage")
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        
        // Verify zero coverage is handled correctly
        val fileResult = runGradle("listFileCoverage")
        assertEquals(TaskOutcome.SUCCESS, fileResult.task(":listFileCoverage")?.outcome)
        assertTrue(fileResult.output.contains("No items match the specified filters.") || fileResult.output.contains("0.00%"))
    }
    
    @Test
    fun `tasks handle missing jacoco report gracefully`() {
        loadFixture("simple-project")
        
        // Try to run coverage tasks without generating report first
        val projectResult = runGradle("listProjectCoverage")
        assertEquals(TaskOutcome.SUCCESS, projectResult.task(":listProjectCoverage")?.outcome)
        
        val fileResult = runGradle("listFileCoverage")
        assertEquals(TaskOutcome.SUCCESS, fileResult.task(":listFileCoverage")?.outcome)
        
        val packageResult = runGradle("listPackageCoverage")
        assertEquals(TaskOutcome.SUCCESS, packageResult.task(":listPackageCoverage")?.outcome)
    }
    
    @Test
    fun `all output formats produce valid content`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        val formats = listOf("table", "json", "csv", "markdown")
        
        for (format in formats) {
            val result = runGradle("listProjectCoverage", "--format=$format")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome, 
                "Format $format should succeed")
            assertTrue(result.output.isNotBlank(), "Format $format should produce output")
        }
    }
}