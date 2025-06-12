package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.aggregator.AggregatedPackageCoverage
import io.github.mpecan.jacoco.aggregator.AggregatedProjectCoverage
import io.github.mpecan.jacoco.model.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertContains

class TableFormatterTest {
    
    private fun createTestCounter(type: CoverageType, covered: Int, missed: Int): CoverageCounter {
        return CoverageCounter(type, missed, covered)
    }
    
    private fun createTestProject(): ProjectCoverageData = ProjectCoverageData("TestProject", createCounters())

    private fun createAggregatedProject(): AggregatedProjectCoverage = AggregatedProjectCoverage("AggregatedProject", createCounters(),1,1,1)

    private fun createAggregatedPackage(): List<AggregatedPackageCoverage> =
        listOf(AggregatedPackageCoverage("AggregatedPackage", createCounters(), 1,1))

    private fun createCounters(): Map<CoverageType, CoverageCounter> = mapOf(
        CoverageType.INSTRUCTION to createTestCounter(CoverageType.INSTRUCTION, 100, 20),
        CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 80, 40),
        CoverageType.LINE to createTestCounter(CoverageType.LINE, 90, 10),
        CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 15, 5),
        CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 8, 2)
    )

    @Test
    fun `should format project coverage without colors`() {
        val formatter = TableFormatter(colorOutput = false)
        val project = createTestProject()
        
        val output = formatter.format(project)
        
        assertContains(output, "PROJECT COVERAGE SUMMARY")
        assertContains(output, "TestProject")
        assertContains(output, "INSTRUCTION")
        assertContains(output, "83.3%")
        assertContains(output, "LINE")
        assertContains(output, "90.0%")
    }
    
    @Test
    fun `should format project coverage with colors`() {
        val formatter = TableFormatter(colorOutput = true)
        val project = createTestProject()
        
        val output = formatter.format(project)
        
        // Should contain ANSI color codes
        assertContains(output, "\u001B[")
        assertContains(output, "PROJECT COVERAGE SUMMARY")
    }

    @Test
    fun `should format aggregated project coverage with colors`() {
        val formatter = TableFormatter(colorOutput = true)
        val project = createAggregatedProject()
        val output = formatter.format(project)
        assertContains(output, "\u001B[")
        assertContains(output, "PROJECT COVERAGE SUMMARY")
    }

    @Test
    fun `should format aggregated package coverage with colors`() {
        val formatter = TableFormatter(colorOutput = true)
        val project = createAggregatedPackage()
        val output = formatter.format(project)
        assertContains(output, "\u001B[")
        assertContains(output, "PACKAGE COVERAGE")
    }

    @Test
    fun `should format empty list`() {
        val formatter = TableFormatter(colorOutput = false)
        val emptyList = emptyList<PackageCoverageData>()
        
        val output = formatter.format(emptyList)
        
        assertContains(output, "No items match the specified filters")
    }
    
    @Test
    fun `should format package list`() {
        val formatter = TableFormatter(colorOutput = false)
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 5, 1),
            CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 20, 5),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 100, 20),
            CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 40, 10)
        )
        
        val packages = listOf(
            PackageCoverageData("com.example.service", counters),
            PackageCoverageData("com.example.controller", counters)
        )
        
        val output = formatter.format(packages)
        
        assertContains(output, "PACKAGE COVERAGE")
        assertContains(output, "com.example.service")
        assertContains(output, "com.example.controller")
        assertContains(output, "83.3%") // Class coverage
        assertContains(output, "80.0%") // Method/Line/Branch coverage
    }
    
    @Test
    fun `should format class list`() {
        val formatter = TableFormatter(colorOutput = false)
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0),
            CoverageType.METHOD to createTestCounter(CoverageType.METHOD, 10, 2),
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 50, 10),
            CoverageType.BRANCH to createTestCounter(CoverageType.BRANCH, 20, 5)
        )
        
        val classes = listOf(
            ClassCoverageData("com.example.UserService", counters, "UserService.java"),
            ClassCoverageData("com.example.ProductController", counters, "ProductController.java")
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "FILE COVERAGE")
        assertContains(output, "com.example.UserService")
        assertContains(output, "UserService.java")
        assertContains(output, "100.0%") // Class coverage
        assertContains(output, "83.3%") // Method coverage
    }
    
    @Test
    fun `should truncate long names`() {
        val formatter = TableFormatter(colorOutput = false)
        
        val counters = mapOf(
            CoverageType.CLASS to createTestCounter(CoverageType.CLASS, 1, 0)
        )
        
        val longClassName = "com.example.this.is.a.very.long.package.name.that.should.be.truncated.UserService"
        val classes = listOf(
            ClassCoverageData(longClassName, counters, "UserService.java")
        )
        
        val output = formatter.format(classes)
        
        assertContains(output, "...")
        assertTrue(output.contains(longClassName.substring(0, 20)))
    }
    
    @Test
    fun `should handle missing counters`() {
        val formatter = TableFormatter(colorOutput = false)
        
        // Only LINE coverage available
        val counters = mapOf(
            CoverageType.LINE to createTestCounter(CoverageType.LINE, 50, 10)
        )
        
        val packages = listOf(
            PackageCoverageData("com.example", counters)
        )
        
        val output = formatter.format(packages)
        
        assertContains(output, "N/A") // For missing coverage types
        assertContains(output, "83.3%") // Line coverage
    }
}