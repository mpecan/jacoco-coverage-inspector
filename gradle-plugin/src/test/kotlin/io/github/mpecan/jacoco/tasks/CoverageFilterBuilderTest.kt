package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.model.CoverageType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoverageFilterBuilderTest {
    
    private val builder = CoverageFilterBuilder()
    
    @Test
    fun `builds empty filter with no parameters`() {
        val filter = builder.buildFilter()
        
        assertTrue(filter.minThresholds.isEmpty())
        assertTrue(filter.maxThresholds.isEmpty())
        assertTrue(filter.includePatterns.isEmpty())
        assertTrue(filter.excludePatterns.isEmpty())
    }
    
    @Test
    fun `applies generic minCoverage to LINE type by default`() {
        val filter = builder.buildFilter(minCoverage = 80.0)
        
        assertEquals(0.8, filter.minThresholds[CoverageType.LINE])
        assertEquals(1, filter.minThresholds.size)
    }
    
    @Test
    fun `applies generic minCoverage to specified coverage type`() {
        val filter = builder.buildFilter(
            minCoverage = 75.0, 
            coverageType = CoverageType.BRANCH
        )
        
        assertEquals(0.75, filter.minThresholds[CoverageType.BRANCH])
        assertEquals(1, filter.minThresholds.size)
    }
    
    @Test
    fun `specific coverage thresholds override generic minCoverage`() {
        val thresholds = CoverageThresholds(minLine = 90.0)
        val filter = builder.buildFilter(
            minCoverage = 80.0,
            coverageType = CoverageType.LINE,
            thresholds = thresholds
        )
        
        // Specific minLineCoverage should override generic minCoverage
        assertEquals(0.9, filter.minThresholds[CoverageType.LINE])
        assertEquals(1, filter.minThresholds.size)
    }
    
    @Test
    fun `builds all min thresholds correctly`() {
        val thresholds = CoverageThresholds(
            minClass = 100.0,
            minMethod = 90.0,
            minLine = 80.0,
            minBranch = 70.0,
            minInstruction = 85.0,
            minComplexity = 75.0
        )
        val filter = builder.buildFilter(thresholds = thresholds)
        
        assertEquals(1.0, filter.minThresholds[CoverageType.CLASS])
        assertEquals(0.9, filter.minThresholds[CoverageType.METHOD])
        assertEquals(0.8, filter.minThresholds[CoverageType.LINE])
        assertEquals(0.7, filter.minThresholds[CoverageType.BRANCH])
        assertEquals(0.85, filter.minThresholds[CoverageType.INSTRUCTION])
        assertEquals(0.75, filter.minThresholds[CoverageType.COMPLEXITY])
        assertEquals(6, filter.minThresholds.size)
    }
    
    @Test
    fun `builds all max thresholds correctly`() {
        val thresholds = CoverageThresholds(
            maxClass = 100.0,
            maxMethod = 95.0,
            maxLine = 90.0,
            maxBranch = 85.0,
            maxInstruction = 92.0,
            maxComplexity = 88.0
        )
        val filter = builder.buildFilter(thresholds = thresholds)
        
        assertEquals(1.0, filter.maxThresholds[CoverageType.CLASS])
        assertEquals(0.95, filter.maxThresholds[CoverageType.METHOD])
        assertEquals(0.9, filter.maxThresholds[CoverageType.LINE])
        assertEquals(0.85, filter.maxThresholds[CoverageType.BRANCH])
        assertEquals(0.92, filter.maxThresholds[CoverageType.INSTRUCTION])
        assertEquals(0.88, filter.maxThresholds[CoverageType.COMPLEXITY])
        assertEquals(6, filter.maxThresholds.size)
    }
    
    @Test
    fun `handles include and exclude patterns`() {
        val filterPatterns = FilterPatterns(
            includePatterns = listOf("com.example.*", "com.test.*"),
            excludePatterns = listOf("*.Test*", "*Mock*")
        )
        val filter = builder.buildFilter(filterPatterns = filterPatterns)
        
        assertEquals(listOf("com.example.*", "com.test.*"), filter.includePatterns)
        assertEquals(listOf("*.Test*", "*Mock*"), filter.excludePatterns)
    }
    
    @Test
    fun `package filter is added to include patterns`() {
        val filterPatterns = FilterPatterns(
            packageFilter = "com.example",
            includePatterns = listOf("existing.pattern")
        )
        val filter = builder.buildFilter(filterPatterns = filterPatterns)
        
        assertTrue(filter.includePatterns.contains("existing.pattern"))
        assertTrue(filter.includePatterns.contains("com.example*"))
        assertEquals(2, filter.includePatterns.size)
    }
    
    @Test
    fun `package filter works without existing include patterns`() {
        val filterPatterns = FilterPatterns(packageFilter = "com.test")
        val filter = builder.buildFilter(filterPatterns = filterPatterns)
        
        assertEquals(listOf("com.test*"), filter.includePatterns)
    }
    
    @Test
    fun `builds complex filter with all options`() {
        val thresholds = CoverageThresholds(
            minLine = 80.0,  // Should override generic minCoverage for LINE
            maxBranch = 95.0
        )
        val filterPatterns = FilterPatterns(
            includePatterns = listOf("com.main.*"),
            excludePatterns = listOf("*Test*"),
            packageFilter = "com.util"
        )
        val filter = builder.buildFilter(
            minCoverage = 50.0,
            coverageType = CoverageType.INSTRUCTION,
            thresholds = thresholds,
            filterPatterns = filterPatterns
        )
        
        // Generic minCoverage applied to INSTRUCTION type
        assertEquals(0.5, filter.minThresholds[CoverageType.INSTRUCTION])
        // Specific minLineCoverage overrides for LINE type
        assertEquals(0.8, filter.minThresholds[CoverageType.LINE])
        assertEquals(2, filter.minThresholds.size)
        
        assertEquals(0.95, filter.maxThresholds[CoverageType.BRANCH])
        assertEquals(1, filter.maxThresholds.size)
        
        assertTrue(filter.includePatterns.contains("com.main.*"))
        assertTrue(filter.includePatterns.contains("com.util*"))
        assertEquals(2, filter.includePatterns.size)
        
        assertEquals(listOf("*Test*"), filter.excludePatterns)
    }
}