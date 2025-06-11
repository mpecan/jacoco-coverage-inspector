package io.github.mpecan.jacoco.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoverageFilterTest {
    
    private fun createTestCoverageData(
        name: String,
        lineCoverage: Double = 0.8,
        branchCoverage: Double = 0.7
    ): CoverageData {
        val counters = mapOf(
            CoverageType.LINE to CoverageCounter(CoverageType.LINE, (100 * (1 - lineCoverage)).toInt(), (100 * lineCoverage).toInt()),
            CoverageType.BRANCH to CoverageCounter(CoverageType.BRANCH, (100 * (1 - branchCoverage)).toInt(), (100 * branchCoverage).toInt())
        )
        return ClassCoverageData(name, counters)
    }
    
    @Test
    fun `should match when no filters specified`() {
        val filter = CoverageFilter()
        val data = createTestCoverageData("com.example.TestClass")
        
        assertTrue(filter.matches(data))
    }
    
    @Test
    fun `should filter by minimum threshold`() {
        val filter = CoverageFilter(
            minThresholds = mapOf(CoverageType.LINE to 0.75)
        )
        
        val highCoverage = createTestCoverageData("HighCoverage", lineCoverage = 0.8)
        val lowCoverage = createTestCoverageData("LowCoverage", lineCoverage = 0.6)
        
        assertTrue(filter.matches(highCoverage))
        assertFalse(filter.matches(lowCoverage))
    }
    
    @Test
    fun `should filter by maximum threshold`() {
        val filter = CoverageFilter(
            maxThresholds = mapOf(CoverageType.LINE to 0.75)
        )
        
        val highCoverage = createTestCoverageData("HighCoverage", lineCoverage = 0.8)
        val lowCoverage = createTestCoverageData("LowCoverage", lineCoverage = 0.6)
        
        assertFalse(filter.matches(highCoverage))
        assertTrue(filter.matches(lowCoverage))
    }
    
    @Test
    fun `should filter by include patterns`() {
        val filter = CoverageFilter(
            includePatterns = listOf("com.example.*")
        )
        
        val included = createTestCoverageData("com.example.TestClass")
        val excluded = createTestCoverageData("com.other.TestClass")
        
        assertTrue(filter.matches(included))
        assertFalse(filter.matches(excluded))
    }
    
    @Test
    fun `should filter by exclude patterns`() {
        val filter = CoverageFilter(
            excludePatterns = listOf("*Test*")
        )
        
        val included = createTestCoverageData("com.example.ProductionClass")
        val excluded = createTestCoverageData("com.example.TestClass")
        
        assertTrue(filter.matches(included))
        assertFalse(filter.matches(excluded))
    }
    
    @Test
    fun `should combine multiple filters with AND logic`() {
        val filter = CoverageFilter(
            minThresholds = mapOf(CoverageType.LINE to 0.75),
            includePatterns = listOf("com.example.*"),
            excludePatterns = listOf("*Test*")
        )
        
        // Matches all criteria
        val validClass = createTestCoverageData("com.example.ProductionClass", lineCoverage = 0.8)
        assertTrue(filter.matches(validClass))
        
        // Fails coverage threshold
        val lowCoverage = createTestCoverageData("com.example.ProductionClass", lineCoverage = 0.6)
        assertFalse(filter.matches(lowCoverage))
        
        // Fails include pattern
        val wrongPackage = createTestCoverageData("com.other.ProductionClass", lineCoverage = 0.8)
        assertFalse(filter.matches(wrongPackage))
        
        // Fails exclude pattern
        val testClass = createTestCoverageData("com.example.TestClass", lineCoverage = 0.8)
        assertFalse(filter.matches(testClass))
    }
    
    @Test
    fun `should handle wildcard patterns correctly`() {
        val filter = CoverageFilter(
            includePatterns = listOf("com.example.*.service.*")
        )
        
        val matches = createTestCoverageData("com.example.user.service.UserService")
        val noMatch = createTestCoverageData("com.example.controller.UserController")
        
        assertTrue(filter.matches(matches))
        assertFalse(filter.matches(noMatch))
    }
    
    @Test
    fun `should handle multiple thresholds`() {
        val filter = CoverageFilter(
            minThresholds = mapOf(
                CoverageType.LINE to 0.8,
                CoverageType.BRANCH to 0.7
            )
        )
        
        val bothHigh = createTestCoverageData("BothHigh", lineCoverage = 0.85, branchCoverage = 0.75)
        val lineLow = createTestCoverageData("LineLow", lineCoverage = 0.75, branchCoverage = 0.75)
        val branchLow = createTestCoverageData("BranchLow", lineCoverage = 0.85, branchCoverage = 0.65)
        
        assertTrue(filter.matches(bothHigh))
        assertFalse(filter.matches(lineLow))
        assertFalse(filter.matches(branchLow))
    }
}