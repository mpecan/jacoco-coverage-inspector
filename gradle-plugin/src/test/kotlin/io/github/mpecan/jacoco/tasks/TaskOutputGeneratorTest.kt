package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.model.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskOutputGeneratorTest {
    
    private val generator = OutputGenerator()
    
    private fun createTestCoverageData(): ProjectCoverageData {
        val classCounters1 = mapOf(
            CoverageType.LINE to CoverageCounter(CoverageType.LINE, 2, 8),
            CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, 1, 3)
        )
        val classCounters2 = mapOf(
            CoverageType.LINE to CoverageCounter(CoverageType.LINE, 5, 5),
            CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, 0, 2)
        )
        val classCounters3 = mapOf(
            CoverageType.LINE to CoverageCounter(CoverageType.LINE, 0, 10),
            CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, 0, 4)
        )
        
        val class1 = ClassCoverageData("com.example.Service", classCounters1, "Service.kt", emptyList())
        val class2 = ClassCoverageData("com.example.Helper", classCounters2, "Helper.kt", emptyList())
        val class3 = ClassCoverageData("com.test.TestUtils", classCounters3, "TestUtils.kt", emptyList())
        
        val package1 = PackageCoverageData("com.example", emptyMap(), listOf(class1, class2))
        val package2 = PackageCoverageData("com.test", emptyMap(), listOf(class3))
        
        return ProjectCoverageData(
            projectName = "test-project",
            projectCounters = emptyMap(),
            packages = listOf(package1, package2)
        )
    }
    
    @Test
    fun `generateProjectOutput returns project data`() {
        val coverageData = createTestCoverageData()
        val filter = CoverageFilter()
        
        val result = generator.generateProjectOutput(coverageData, filter)
        
        assertEquals(coverageData, result)
    }
    
    @Test
    fun `generateFileOutput returns all classes sorted by name`() {
        val coverageData = createTestCoverageData()
        val filter = CoverageFilter() // No filtering
        
        val result = generator.generateFileOutput(coverageData, filter)
        
        assertEquals(3, result.size)
        assertEquals("com.example.Helper", result[0].className)
        assertEquals("com.example.Service", result[1].className)
        assertEquals("com.test.TestUtils", result[2].className)
    }
    
    @Test
    fun `generateFileOutput applies filtering correctly`() {
        val coverageData = createTestCoverageData()
        
        // Filter for classes with 100% line coverage
        val filter = CoverageFilter(
            minThresholds = mapOf(CoverageType.LINE to 1.0)
        )
        
        val result = generator.generateFileOutput(coverageData, filter)
        
        // Only com.test.TestUtils should match (100% line coverage)
        assertEquals(1, result.size)
        assertTrue(result.any { it.className == "com.test.TestUtils" })
    }
    
    @Test
    fun `generateFileOutput with include patterns`() {
        val coverageData = createTestCoverageData()
        
        // Filter for classes in com.example package only
        val filter = CoverageFilter(
            includePatterns = listOf("com.example.*")
        )
        
        val result = generator.generateFileOutput(coverageData, filter)
        
        assertEquals(2, result.size)
        assertTrue(result.all { it.className.startsWith("com.example") })
    }
    
    @Test
    fun `generateFileOutput with exclude patterns`() {
        val coverageData = createTestCoverageData()
        
        // Exclude test classes
        val filter = CoverageFilter(
            excludePatterns = listOf("*Test*")
        )
        
        val result = generator.generateFileOutput(coverageData, filter)
        
        assertEquals(2, result.size)
        assertTrue(result.none { it.className.contains("Test") })
    }
    
    @Test
    fun `generatePackageOutput returns filtered packages sorted by name`() {
        val coverageData = createTestCoverageData()
        val filter = CoverageFilter() // No filtering
        
        val result = generator.generatePackageOutput(coverageData, filter)
        
        assertEquals(2, result.size)
        assertEquals("com.example", result[0].packageName)
        assertEquals("com.test", result[1].packageName)
    }
    
    @Test
    fun `generatePackageOutput applies filtering correctly`() {
        val coverageData = createTestCoverageData()
        
        // Filter for packages with specific pattern
        val filter = CoverageFilter(
            includePatterns = listOf("com.example*")
        )
        
        val result = generator.generatePackageOutput(coverageData, filter)
        
        assertEquals(1, result.size)
        assertEquals("com.example", result[0].packageName)
    }
    
    @Test
    fun `generateFileOutput returns empty list when no classes match filter`() {
        val coverageData = createTestCoverageData()
        
        // Filter that no class will match (impossible line coverage)
        val filter = CoverageFilter(
            minThresholds = mapOf(CoverageType.LINE to 2.0) // 200% coverage impossible
        )
        
        val result = generator.generateFileOutput(coverageData, filter)
        
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `generatePackageOutput returns empty list when no packages match filter`() {
        val coverageData = createTestCoverageData()
        
        // Filter that no package will match
        val filter = CoverageFilter(
            includePatterns = listOf("nonexistent.*")
        )
        
        val result = generator.generatePackageOutput(coverageData, filter)
        
        assertTrue(result.isEmpty())
    }
}