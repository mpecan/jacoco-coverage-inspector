package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.aggregator.*
import io.github.mpecan.jacoco.model.*
import kotlin.test.*

class CsvFormatterAggregatedTest {
    
    private val formatter = CsvFormatter()
    
    @Test
    fun `should format AggregatedProjectCoverage with correct headers and data`() {
        val project = AggregatedProjectCoverage(
            projectName = "MyProject",
            totalCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 100, covered = 400),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 50, covered = 150),
                CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 5, covered = 45),
                CoverageType.CLASS to CoverageCounter(CoverageType.CLASS, missed = 2, covered = 18)
            ),
            packageCount = 3,
            classCount = 20,
            methodCount = 50
        )
        
        val result = formatter.format(project)
        
        // Verify headers include metadata columns
        assertTrue(result.contains("Type,Name,PackageCount,ClassCount,MethodCount"))
        
        // Verify data row
        assertTrue(result.contains("PROJECT,MyProject,3,20,50"))
        
        // Verify coverage data
        assertTrue(result.contains("400,100,500,80.00")) // Line coverage
        assertTrue(result.contains("150,50,200,75.00"))  // Branch coverage
    }
    
    @Test
    fun `should format list of AggregatedPackageCoverage correctly`() {
        val packages = listOf(
            AggregatedPackageCoverage(
                packageName = "com.example.core",
                aggregatedCounters = mapOf(
                    CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 20, covered = 80),
                    CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 2, covered = 18)
                ),
                classCount = 5,
                methodCount = 20
            ),
            AggregatedPackageCoverage(
                packageName = "com.example.util",
                aggregatedCounters = mapOf(
                    CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 30, covered = 70),
                    CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 3, covered = 12)
                ),
                classCount = 3,
                methodCount = 15
            )
        )
        
        val result = formatter.format(packages)
        val lines = result.trim().split("\n")
        
        // Verify header
        assertTrue(lines[0].contains("Type,Name,ClassCount,MethodCount"))
        
        // Verify data rows
        assertEquals(3, lines.size) // Header + 2 data rows
        assertTrue(lines[1].contains("PACKAGE,com.example.core,5,20"))
        assertTrue(lines[1].contains("80,20,100,80.00")) // Line coverage
        
        assertTrue(lines[2].contains("PACKAGE,com.example.util,3,15"))
        assertTrue(lines[2].contains("70,30,100,70.00")) // Line coverage
    }
    
    @Test
    fun `should escape special characters in CSV output`() {
        val project = AggregatedProjectCoverage(
            projectName = "Project with, commas and \"quotes\"",
            totalCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 10, covered = 90)
            ),
            packageCount = 1,
            classCount = 1,
            methodCount = 1
        )
        
        val result = formatter.format(project)
        
        // Verify proper escaping
        assertTrue(result.contains("\"Project with, commas and \"\"quotes\"\"\""))
    }
    
    @Test
    fun `should handle empty counters gracefully`() {
        val packages = listOf(
            AggregatedPackageCoverage(
                packageName = "com.empty",
                aggregatedCounters = emptyMap(),
                classCount = 0,
                methodCount = 0
            )
        )
        
        val result = formatter.format(packages)
        val lines = result.trim().split("\n")
        
        // Should have header and one data row
        assertEquals(2, lines.size)
        
        // Verify all counter values are zeros
        val dataRow = lines[1]
        assertTrue(dataRow.contains("PACKAGE,com.empty,0,0"))
        // Each coverage type should show 0,0,0,0.00
        val zeroPattern = "0,0,0,0.00"
        assertEquals(6, dataRow.split(zeroPattern).size - 1) // 6 coverage types
    }
    
    @Test
    fun `should maintain consistent column order for all coverage types`() {
        val project = AggregatedProjectCoverage(
            projectName = "Test",
            totalCounters = CoverageType.entries.associateWith { type ->
                CoverageCounter(type, missed = 10, covered = 90)
            },
            packageCount = 1,
            classCount = 10,
            methodCount = 50
        )
        
        val result = formatter.format(project)
        val lines = result.trim().split("\n")
        val header = lines[0]
        
        // Verify all coverage types are in header in correct order
        assertTrue(header.contains("ClassCovered,ClassMissed,ClassTotal,ClassPercentage"))
        assertTrue(header.contains("MethodCovered,MethodMissed,MethodTotal,MethodPercentage"))
        assertTrue(header.contains("LineCovered,LineMissed,LineTotal,LinePercentage"))
        assertTrue(header.contains("BranchCovered,BranchMissed,BranchTotal,BranchPercentage"))
        assertTrue(header.contains("InstructionCovered,InstructionMissed,InstructionTotal,InstructionPercentage"))
        assertTrue(header.contains("ComplexityCovered,ComplexityMissed,ComplexityTotal,ComplexityPercentage"))
    }
}