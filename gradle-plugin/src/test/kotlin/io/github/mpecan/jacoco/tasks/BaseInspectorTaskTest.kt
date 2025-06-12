package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.PluginFixtureTest
import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaseInspectorTaskTest : PluginFixtureTest() {
    
    @Test
    fun `test all output formats work correctly`() {
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
    
    @Test
    fun `test coverage type filtering works`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        val coverageTypes = listOf("INSTRUCTION", "BRANCH", "LINE", "COMPLEXITY", "METHOD", "CLASS")
        
        for (type in coverageTypes) {
            val result = runGradle("listFileCoverage", "--coverageType=$type")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome,
                "Coverage type $type should work")
        }
    }
    
    @Test
    fun `test minimum coverage thresholds work`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        // Test generic minCoverage
        val result1 = runGradle("listFileCoverage", "--minCoverage=50")
        assertEquals(TaskOutcome.SUCCESS, result1.task(":listFileCoverage")?.outcome)
        
        // Test specific coverage thresholds
        val result2 = runGradle("listFileCoverage", "--minLineCoverage=80")
        assertEquals(TaskOutcome.SUCCESS, result2.task(":listFileCoverage")?.outcome)
        
        val result3 = runGradle("listFileCoverage", "--minBranchCoverage=70")
        assertEquals(TaskOutcome.SUCCESS, result3.task(":listFileCoverage")?.outcome)
        
        val result4 = runGradle("listFileCoverage", "--minMethodCoverage=60")
        assertEquals(TaskOutcome.SUCCESS, result4.task(":listFileCoverage")?.outcome)
        
        val result5 = runGradle("listFileCoverage", "--minClassCoverage=90")
        assertEquals(TaskOutcome.SUCCESS, result5.task(":listFileCoverage")?.outcome)
        
        val result6 = runGradle("listFileCoverage", "--minInstructionCoverage=85")
        assertEquals(TaskOutcome.SUCCESS, result6.task(":listFileCoverage")?.outcome)
        
        val result7 = runGradle("listFileCoverage", "--minComplexityCoverage=75")
        assertEquals(TaskOutcome.SUCCESS, result7.task(":listFileCoverage")?.outcome)
    }
    
    @Test
    fun `test maximum coverage thresholds work`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        // Test specific maximum coverage thresholds
        val result1 = runGradle("listFileCoverage", "--maxLineCoverage=100")
        assertEquals(TaskOutcome.SUCCESS, result1.task(":listFileCoverage")?.outcome)
        
        val result2 = runGradle("listFileCoverage", "--maxBranchCoverage=100")
        assertEquals(TaskOutcome.SUCCESS, result2.task(":listFileCoverage")?.outcome)
        
        val result3 = runGradle("listFileCoverage", "--maxMethodCoverage=100")
        assertEquals(TaskOutcome.SUCCESS, result3.task(":listFileCoverage")?.outcome)
        
        val result4 = runGradle("listFileCoverage", "--maxClassCoverage=100")
        assertEquals(TaskOutcome.SUCCESS, result4.task(":listFileCoverage")?.outcome)
        
        val result5 = runGradle("listFileCoverage", "--maxInstructionCoverage=100")
        assertEquals(TaskOutcome.SUCCESS, result5.task(":listFileCoverage")?.outcome)
        
        val result6 = runGradle("listFileCoverage", "--maxComplexityCoverage=100")
        assertEquals(TaskOutcome.SUCCESS, result6.task(":listFileCoverage")?.outcome)
    }
    
    @Test
    fun `test package filtering works`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        val result = runGradle("listFileCoverage", "--packageFilter=com.test")
        assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        assertTrue(result.output.isNotBlank(), "Package filter should produce output")
    }
    
    @Test
    fun `test include and exclude patterns work`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        val result1 = runGradle("listFileCoverage", "--includePatterns=com.test.*")
        assertEquals(TaskOutcome.SUCCESS, result1.task(":listFileCoverage")?.outcome)
        
        val result2 = runGradle("listFileCoverage", "--excludePatterns=*.Test*")
        assertEquals(TaskOutcome.SUCCESS, result2.task(":listFileCoverage")?.outcome)
    }
    
    @Test
    fun `test color output option works`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        val result1 = runGradle("listProjectCoverage", "--color")
        assertEquals(TaskOutcome.SUCCESS, result1.task(":listProjectCoverage")?.outcome)
        
        val result2 = runGradle("listProjectCoverage", "--no-color")
        assertEquals(TaskOutcome.SUCCESS, result2.task(":listProjectCoverage")?.outcome)
    }
    
    @Test
    fun `test combination of filters works`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        val result = runGradle("listFileCoverage", 
            "--minCoverage=0", 
            "--coverageType=LINE",
            "--packageFilter=com.test",
            "--format=json"
        )
        assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        assertTrue(result.output.contains("\"className\""), "Should contain JSON formatted output")
    }
    
    @Test
    fun `test extension configuration is applied`() {
        loadFixture("simple-project")
        
        // Modify build file to configure extension
        val buildFile = testProjectDir.resolve("build.gradle.kts")
        val originalContent = buildFile.readText()
        buildFile.writeText(originalContent + """
            
            jacocoInspector {
                defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.JSON
                colorOutput = false
                minLineCoverage = 50.0
                maxLineCoverage = 100.0
                includePatterns = listOf("com.test.*")
                excludePatterns = listOf("*.Test*")
            }
        """.trimIndent())
        
        runGradle("test", "jacocoTestReport")
        val result = runGradle("listProjectCoverage")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        // Should use JSON format from extension
        assertTrue(result.output.contains("\"projectName\""), "Should use JSON format from extension")
    }
    
    @Test
    fun `test command line options override extension`() {
        loadFixture("simple-project")
        
        // Modify build file to configure extension with table format
        val buildFile = testProjectDir.resolve("build.gradle.kts")
        val originalContent = buildFile.readText()
        buildFile.writeText(originalContent + """
            
            jacocoInspector {
                defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.TABLE
                colorOutput = true
            }
        """.trimIndent())
        
        runGradle("test", "jacocoTestReport")
        
        // Override with JSON format
        val result = runGradle("listProjectCoverage", "--format=json")
        
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        // Should use JSON format from command line, overriding extension
        assertTrue(result.output.contains("\"projectName\""), "Should use JSON format from command line")
    }
    
    @Test
    fun `test generic minCoverage with different coverage types`() {
        loadFixture("simple-project")
        runGradle("test", "jacocoTestReport")
        
        // Test generic minCoverage with explicit coverage type
        val result1 = runGradle("listFileCoverage", "--minCoverage=50", "--coverageType=BRANCH")
        assertEquals(TaskOutcome.SUCCESS, result1.task(":listFileCoverage")?.outcome)
        
        // Test generic minCoverage defaults to LINE when no coverage type specified
        val result2 = runGradle("listFileCoverage", "--minCoverage=50")
        assertEquals(TaskOutcome.SUCCESS, result2.task(":listFileCoverage")?.outcome)
    }
    
    @Test
    fun `test non-existent jacoco report is handled gracefully`() {
        loadFixture("simple-project")
        
        // Don't generate jacoco report, just run the task
        val result = runGradle("listProjectCoverage")
        assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        // Should handle missing report gracefully
        assertTrue(result.output.isNotBlank(), "Should produce some output even without report")
    }
}