package io.github.mpecan.jacoco.aggregator

import io.github.mpecan.jacoco.model.*
import kotlin.test.*

class AggregatorIntegrationTest {
    
    @Test
    fun `should correctly aggregate real-world scenario`() {
        // Create a realistic project structure
        val method1 = MethodCoverageData(
            methodName = "calculateTotal",
            methodCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 2, covered = 8),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 1, covered = 3)
            )
        )
        
        val method2 = MethodCoverageData(
            methodName = "validateInput",
            methodCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 5, covered = 5),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 2, covered = 2)
            )
        )
        
        val class1 = ClassCoverageData(
            className = "Calculator",
            classCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 7, covered = 13),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 3, covered = 5),
                CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 0, covered = 2),
                CoverageType.CLASS to CoverageCounter(CoverageType.CLASS, missed = 0, covered = 1)
            ),
            methods = listOf(method1, method2)
        )
        
        val package1 = PackageCoverageData(
            packageName = "com.example.calc",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 7, covered = 13),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 3, covered = 5),
                CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 0, covered = 2),
                CoverageType.CLASS to CoverageCounter(CoverageType.CLASS, missed = 0, covered = 1)
            ),
            classes = listOf(class1)
        )
        
        val project = ProjectCoverageData(
            projectName = "CalculatorApp",
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 7, covered = 13),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 3, covered = 5),
                CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 0, covered = 2),
                CoverageType.CLASS to CoverageCounter(CoverageType.CLASS, missed = 0, covered = 1),
                CoverageType.INSTRUCTION to CoverageCounter(CoverageType.INSTRUCTION, missed = 50, covered = 150),
                CoverageType.COMPLEXITY to CoverageCounter(CoverageType.COMPLEXITY, missed = 2, covered = 8)
            ),
            packages = listOf(package1)
        )
        
        // Test ProjectCoverageAggregator
        val projectAggregator = ProjectCoverageAggregator()
        val projectResult = projectAggregator.aggregateProject(project)
        
        assertEquals("CalculatorApp", projectResult.projectName)
        assertEquals(1, projectResult.packageCount)
        assertEquals(1, projectResult.classCount)
        assertEquals(2, projectResult.methodCount)
        
        // Verify line coverage
        val lineCounter = projectResult.totalCounters[CoverageType.LINE]
        assertNotNull(lineCounter)
        assertEquals(65.0, lineCounter.percentage, 0.1) // 13/(13+7) * 100
        
        // Verify branch coverage
        val branchCounter = projectResult.totalCounters[CoverageType.BRANCH]
        assertNotNull(branchCounter)
        assertEquals(62.5, branchCounter.percentage, 0.1) // 5/(5+3) * 100
        
        // Test PackageCoverageAggregator
        val packageAggregator = PackageCoverageAggregator()
        val packageResults = packageAggregator.aggregatePackages(project)
        
        assertEquals(1, packageResults.size)
        val packageResult = packageResults.first()
        
        assertEquals("com.example.calc", packageResult.packageName)
        assertEquals(1, packageResult.classCount)
        assertEquals(2, packageResult.methodCount)
        assertEquals(65.0, packageResult.aggregatedCounters[CoverageType.LINE]?.percentage ?: 0.0, 0.1)
    }
    
    @Test
    fun `should handle coverage filtering correctly`() {
        val wellCoveredPackage = PackageCoverageData(
            packageName = "com.example.good",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 10, covered = 90)
            ),
            classes = emptyList()
        )
        
        val poorlyCoveredPackage = PackageCoverageData(
            packageName = "com.example.bad",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 80, covered = 20)
            ),
            classes = emptyList()
        )
        
        val project = ProjectCoverageData(
            projectName = "MixedCoverageProject",
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 90, covered = 110)
            ),
            packages = listOf(wellCoveredPackage, poorlyCoveredPackage)
        )
        
        val aggregator = PackageCoverageAggregator()
        
        // Filter for packages with at least 80% line coverage
        val filter = CoverageFilter(
            minThresholds = mapOf(CoverageType.LINE to 0.8)
        )
        
        val results = aggregator.aggregatePackages(project, filter)
        
        assertEquals(1, results.size)
        assertEquals("com.example.good", results.first().packageName)
    }
}