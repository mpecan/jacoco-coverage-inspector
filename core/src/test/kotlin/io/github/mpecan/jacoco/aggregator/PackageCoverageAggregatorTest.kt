package io.github.mpecan.jacoco.aggregator

import io.github.mpecan.jacoco.model.*
import kotlin.test.*

class PackageCoverageAggregatorTest {
    
    private val aggregator = PackageCoverageAggregator()
    
    @Test
    fun `should aggregate single package correctly`() {
        val packageData = createSamplePackage()
        
        val result = aggregator.aggregatePackage(packageData)
        
        assertEquals("com.example.test", result.packageName)
        assertEquals(2, result.classCount)
        assertEquals(4, result.methodCount)
        
        // Verify counters
        val lineCounter = result.aggregatedCounters[CoverageType.LINE]
        assertNotNull(lineCounter)
        assertEquals(70, lineCounter.covered)
        assertEquals(30, lineCounter.missed)
    }
    
    @Test
    fun `should aggregate packages from project`() {
        val projectData = createSampleProject()
        
        val results = aggregator.aggregatePackages(projectData)
        
        assertEquals(2, results.size)
        assertEquals("com.example.package1", results[0].packageName)
        assertEquals("com.example.package2", results[1].packageName)
        
        // Verify first package
        assertEquals(2, results[0].classCount)
        assertEquals(4, results[0].methodCount)
        
        // Verify second package
        assertEquals(1, results[1].classCount)
        assertEquals(2, results[1].methodCount)
    }
    
    @Test
    fun `should apply filter when aggregating packages`() {
        val projectData = createSampleProject()
        val filter = CoverageFilter(
            includePatterns = listOf("*package1*")
        )
        
        val results = aggregator.aggregatePackages(projectData, filter)
        
        assertEquals(1, results.size)
        assertEquals("com.example.package1", results[0].packageName)
    }
    
    @Test
    fun `should handle empty packages list`() {
        val projectData = ProjectCoverageData(
            projectName = "EmptyProject",
            projectCounters = emptyMap(),
            packages = emptyList()
        )
        
        val results = aggregator.aggregatePackages(projectData)
        
        assertTrue(results.isEmpty())
    }
    
    @Test
    fun `should aggregate packages with same name correctly`() {
        val package1 = createSamplePackage("com.example.shared", 
            classCount = 2, methodsPerClass = 2,
            linesCovered = 60, linesMissed = 40
        )
        val package2 = createSamplePackage("com.example.shared",
            classCount = 3, methodsPerClass = 1,
            linesCovered = 90, linesMissed = 10
        )
        val package3 = createSamplePackage("com.example.unique",
            classCount = 1, methodsPerClass = 3,
            linesCovered = 30, linesMissed = 20
        )
        
        val results = aggregator.aggregatePackagesByName(listOf(package1, package2, package3))
        
        assertEquals(2, results.size)
        
        // Find the aggregated "shared" package
        val sharedPackage = results.find { it.packageName == "com.example.shared" }
        assertNotNull(sharedPackage)
        assertEquals(5, sharedPackage.classCount) // 2 + 3
        assertEquals(7, sharedPackage.methodCount) // 2*2 + 3*1
        
        val lineCounter = sharedPackage.aggregatedCounters[CoverageType.LINE]
        assertNotNull(lineCounter)
        assertEquals(150, lineCounter.covered) // 60 + 90
        assertEquals(50, lineCounter.missed) // 40 + 10
        
        // Verify unique package
        val uniquePackage = results.find { it.packageName == "com.example.unique" }
        assertNotNull(uniquePackage)
        assertEquals(1, uniquePackage.classCount)
        assertEquals(3, uniquePackage.methodCount)
    }
    
    @Test
    fun `should handle packages with no classes`() {
        val packageData = PackageCoverageData(
            packageName = "com.example.empty",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 0, covered = 0)
            ),
            classes = emptyList()
        )
        
        val result = aggregator.aggregatePackage(packageData)
        
        assertEquals("com.example.empty", result.packageName)
        assertEquals(0, result.classCount)
        assertEquals(0, result.methodCount)
    }
    
    @Test
    fun `should preserve all coverage types in aggregation`() {
        val packageData = PackageCoverageData(
            packageName = "com.example.full",
            packageCounters = mapOf(
                CoverageType.INSTRUCTION to CoverageCounter(CoverageType.INSTRUCTION, missed = 100, covered = 900),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 20, covered = 80),
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 50, covered = 150),
                CoverageType.COMPLEXITY to CoverageCounter(CoverageType.COMPLEXITY, missed = 10, covered = 40),
                CoverageType.METHOD to CoverageCounter(CoverageType.METHOD, missed = 5, covered = 15),
                CoverageType.CLASS to CoverageCounter(CoverageType.CLASS, missed = 2, covered = 8)
            ),
            classes = emptyList()
        )
        
        val result = aggregator.aggregatePackage(packageData)
        
        assertEquals(6, result.aggregatedCounters.size)
        CoverageType.entries.forEach { type ->
            assertNotNull(result.aggregatedCounters[type], "Counter for $type should be present")
        }
    }
    
    @Test
    fun `should sort packages by name`() {
        val packages = listOf(
            createSamplePackage("com.zebra"),
            createSamplePackage("com.alpha"),
            createSamplePackage("com.beta")
        )
        
        val project = ProjectCoverageData(
            projectName = "Test",
            projectCounters = emptyMap(),
            packages = packages
        )
        
        val results = aggregator.aggregatePackages(project)
        
        assertEquals("com.alpha", results[0].packageName)
        assertEquals("com.beta", results[1].packageName)
        assertEquals("com.zebra", results[2].packageName)
    }
    
    private fun createSamplePackage(
        name: String = "com.example.test",
        classCount: Int = 2,
        methodsPerClass: Int = 2,
        linesCovered: Int = 70,
        linesMissed: Int = 30
    ): PackageCoverageData {
        val methods = (1..methodsPerClass).map { i ->
            MethodCoverageData(
                methodName = "method$i",
                methodCounters = mapOf(
                    CoverageType.LINE to CoverageCounter(
                        CoverageType.LINE, 
                        missed = linesMissed / (classCount * methodsPerClass),
                        covered = linesCovered / (classCount * methodsPerClass)
                    )
                )
            )
        }
        
        val classes = (1..classCount).map { i ->
            ClassCoverageData(
                className = "Class$i",
                classCounters = mapOf(
                    CoverageType.LINE to CoverageCounter(
                        CoverageType.LINE,
                        missed = linesMissed / classCount,
                        covered = linesCovered / classCount
                    )
                ),
                methods = methods
            )
        }
        
        return PackageCoverageData(
            packageName = name,
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = linesMissed, covered = linesCovered),
                CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, missed = 20, covered = 80)
            ),
            classes = classes
        )
    }
    
    private fun createSampleProject(): ProjectCoverageData {
        val package1 = createSamplePackage("com.example.package1", 2, 2)
        val package2 = createSamplePackage("com.example.package2", 1, 2)
        
        return ProjectCoverageData(
            projectName = "TestProject",
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 100, covered = 200)
            ),
            packages = listOf(package1, package2)
        )
    }
}