package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.aggregator.*
import io.github.mpecan.jacoco.model.*
import kotlin.test.*

class AggregatedDataFormatterTest {
    
    @Test
    fun `TableFormatter should format AggregatedProjectCoverage correctly`() {
        val formatter = TableFormatter(colorOutput = false)
        val aggregatedProject = createSampleAggregatedProject()
        
        val result = formatter.format(aggregatedProject)
        
        // Verify key elements are present
        assertTrue(result.contains("PROJECT COVERAGE SUMMARY"), "Should contain summary header")
        assertTrue(result.contains("TestProject"), "Should contain project name")
        assertTrue(result.contains("Total Packages"), "Should contain packages label")
        assertTrue(result.contains("5"), "Should contain package count")
        assertTrue(result.contains("Total Classes"), "Should contain classes label")
        assertTrue(result.contains("20"), "Should contain class count")
        assertTrue(result.contains("Total Methods"), "Should contain methods label")
        assertTrue(result.contains("100"), "Should contain method count")
        
        // Verify coverage data
        assertTrue(result.contains("LINE"), "Should contain LINE coverage type")
        assertTrue(result.contains("BRANCH"), "Should contain BRANCH coverage type")
        assertTrue(result.contains("80.0%"), "Should contain line coverage percentage")
        assertTrue(result.contains("75.0%"), "Should contain branch coverage percentage")
    }
    
    @Test
    fun `TableFormatter should format list of AggregatedPackageCoverage correctly`() {
        val formatter = TableFormatter(colorOutput = false)
        val packages = listOf(
            createSampleAggregatedPackage("com.example.core", 10, 50, 85.0),
            createSampleAggregatedPackage("com.example.util", 5, 20, 70.0),
            createSampleAggregatedPackage("com.example.test", 3, 15, 95.0)
        )
        
        val result = formatter.format(packages)
        
        // Verify header
        assertTrue(result.contains("PACKAGE COVERAGE"))
        assertTrue(result.contains("Classes"))
        assertTrue(result.contains("Methods"))
        
        // Verify package data
        assertTrue(result.contains("com.example.core"))
        assertTrue(result.contains("10"))  // class count
        assertTrue(result.contains("50"))  // method count
        assertTrue(result.contains("85.0%"))
        
        assertTrue(result.contains("com.example.util"))
        assertTrue(result.contains("5"))
        assertTrue(result.contains("20"))
        assertTrue(result.contains("70.0%"))
        
        assertTrue(result.contains("com.example.test"))
        assertTrue(result.contains("3"))
        assertTrue(result.contains("15"))
        assertTrue(result.contains("95.0%"))
    }
    
    @Test
    fun `JsonFormatter should serialize AggregatedProjectCoverage correctly`() {
        val formatter = JsonFormatter()
        val aggregatedProject = createSampleAggregatedProject()
        
        val result = formatter.format(aggregatedProject)
        
        // Verify JSON structure
        assertTrue(result.contains("\"projectName\": \"TestProject\""))
        assertTrue(result.contains("\"packageCount\": 5"))
        assertTrue(result.contains("\"classCount\": 20"))
        assertTrue(result.contains("\"methodCount\": 100"))
        assertTrue(result.contains("\"totalCounters\""))
        
        // Verify counter data
        assertTrue(result.contains("\"type\": \"LINE\""))
        assertTrue(result.contains("\"covered\": 800"))
        assertTrue(result.contains("\"missed\": 200"))
    }
    
    @Test
    fun `JsonFormatter should serialize list of AggregatedPackageCoverage correctly`() {
        val formatter = JsonFormatter()
        val packages = listOf(
            createSampleAggregatedPackage("com.example.core", 10, 50, 85.0),
            createSampleAggregatedPackage("com.example.util", 5, 20, 70.0)
        )
        
        val result = formatter.format(packages)
        
        // Verify it's a JSON array
        assertTrue(result.startsWith("["))
        assertTrue(result.endsWith("]"))
        
        // Verify package data
        assertTrue(result.contains("\"packageName\": \"com.example.core\""))
        assertTrue(result.contains("\"classCount\": 10"))
        assertTrue(result.contains("\"methodCount\": 50"))
        
        assertTrue(result.contains("\"packageName\": \"com.example.util\""))
        assertTrue(result.contains("\"classCount\": 5"))
        assertTrue(result.contains("\"methodCount\": 20"))
    }
    
    @Test
    fun `formatters should handle empty aggregated data`() {
        val tableFormatter = TableFormatter(colorOutput = false)
        val jsonFormatter = JsonFormatter()
        
        val emptyPackages = emptyList<AggregatedPackageCoverage>()
        
        val tableResult = tableFormatter.format(emptyPackages)
        assertTrue(tableResult.contains("No items match"))
        
        val jsonResult = jsonFormatter.format(emptyPackages)
        assertEquals("[]", jsonResult.trim())
    }
    
    @Test
    fun `TableFormatter should apply color coding when enabled`() {
        val formatter = TableFormatter(colorOutput = true)
        val packages = listOf(
            createSampleAggregatedPackage("com.high", 10, 50, 85.0),    // Green
            createSampleAggregatedPackage("com.medium", 5, 20, 65.0),   // Yellow
            createSampleAggregatedPackage("com.low", 3, 15, 45.0)       // Red
        )
        
        val result = formatter.format(packages)
        
        // Check for ANSI color codes
        assertTrue(result.contains("\u001B[32m"))  // Green
        assertTrue(result.contains("\u001B[33m"))  // Yellow
        assertTrue(result.contains("\u001B[31m"))  // Red
        assertTrue(result.contains("\u001B[0m"))   // Reset
    }
    
    private fun createSampleAggregatedProject(): AggregatedProjectCoverage {
        return AggregatedProjectCoverage(
            projectName = "TestProject",
            totalCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 200, covered = 800),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 100, covered = 300),
                CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 10, covered = 90),
                CoverageType.CLASS to CoverageCounter(CoverageType.CLASS, missed = 2, covered = 18),
                CoverageType.INSTRUCTION to CoverageCounter(CoverageType.INSTRUCTION, missed = 1000, covered = 4000),
                CoverageType.COMPLEXITY to CoverageCounter(CoverageType.COMPLEXITY, missed = 50, covered = 150)
            ),
            packageCount = 5,
            classCount = 20,
            methodCount = 100
        )
    }
    
    private fun createSampleAggregatedPackage(
        name: String,
        classCount: Int,
        methodCount: Int,
        lineCoverage: Double
    ): AggregatedPackageCoverage {
        val lineMissed = ((100 - lineCoverage) * 10).toInt()
        val lineCovered = (lineCoverage * 10).toInt()
        
        return AggregatedPackageCoverage(
            packageName = name,
            aggregatedCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = lineMissed, covered = lineCovered),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 20, covered = 80),
                CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 5, covered = methodCount - 5),
                CoverageType.CLASS to CoverageCounter(CoverageType.CLASS, missed = 1, covered = classCount - 1)
            ),
            classCount = classCount,
            methodCount = methodCount
        )
    }
}