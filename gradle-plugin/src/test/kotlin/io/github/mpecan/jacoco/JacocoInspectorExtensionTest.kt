package io.github.mpecan.jacoco

import io.github.mpecan.jacoco.model.OutputFormat
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JacocoInspectorExtensionTest {
    
    @Test
    fun `extension has correct default values`() {
        val extension = JacocoInspectorExtension()
        
        assertEquals(OutputFormat.TABLE, extension.defaultFormat, "Default format should be TABLE")
        assertTrue(extension.colorOutput, "Color output should be enabled by default")
        
        // All coverage thresholds should be null by default
        assertNull(extension.minClassCoverage, "Min class coverage should be null by default")
        assertNull(extension.minMethodCoverage, "Min method coverage should be null by default")
        assertNull(extension.minLineCoverage, "Min line coverage should be null by default")
        assertNull(extension.minBranchCoverage, "Min branch coverage should be null by default")
        assertNull(extension.minInstructionCoverage, "Min instruction coverage should be null by default")
        assertNull(extension.minComplexityCoverage, "Min complexity coverage should be null by default")
        
        assertNull(extension.maxClassCoverage, "Max class coverage should be null by default")
        assertNull(extension.maxMethodCoverage, "Max method coverage should be null by default")
        assertNull(extension.maxLineCoverage, "Max line coverage should be null by default")
        assertNull(extension.maxBranchCoverage, "Max branch coverage should be null by default")
        assertNull(extension.maxInstructionCoverage, "Max instruction coverage should be null by default")
        assertNull(extension.maxComplexityCoverage, "Max complexity coverage should be null by default")
        
        assertTrue(extension.excludePatterns.isEmpty(), "Exclude patterns should be empty by default")
        assertTrue(extension.includePatterns.isEmpty(), "Include patterns should be empty by default")
    }
    
    @Test
    fun `extension properties can be modified`() {
        val extension = JacocoInspectorExtension()
        
        // Modify format and color output
        extension.defaultFormat = OutputFormat.JSON
        extension.colorOutput = false
        
        assertEquals(OutputFormat.JSON, extension.defaultFormat)
        assertEquals(false, extension.colorOutput)
        
        // Set coverage thresholds
        extension.minClassCoverage = 80.0
        extension.minMethodCoverage = 75.0
        extension.minLineCoverage = 70.0
        extension.minBranchCoverage = 65.0
        extension.minInstructionCoverage = 60.0
        extension.minComplexityCoverage = 55.0
        
        assertEquals(80.0, extension.minClassCoverage)
        assertEquals(75.0, extension.minMethodCoverage)
        assertEquals(70.0, extension.minLineCoverage)
        assertEquals(65.0, extension.minBranchCoverage)
        assertEquals(60.0, extension.minInstructionCoverage)
        assertEquals(55.0, extension.minComplexityCoverage)
        
        // Set max thresholds
        extension.maxClassCoverage = 90.0
        extension.maxMethodCoverage = 85.0
        extension.maxLineCoverage = 80.0
        extension.maxBranchCoverage = 75.0
        extension.maxInstructionCoverage = 70.0
        extension.maxComplexityCoverage = 65.0
        
        assertEquals(90.0, extension.maxClassCoverage)
        assertEquals(85.0, extension.maxMethodCoverage)
        assertEquals(80.0, extension.maxLineCoverage)
        assertEquals(75.0, extension.maxBranchCoverage)
        assertEquals(70.0, extension.maxInstructionCoverage)
        assertEquals(65.0, extension.maxComplexityCoverage)
        
        // Set patterns
        extension.includePatterns = listOf("**/src/**", "**/main/**")
        extension.excludePatterns = listOf("**/test/**", "**/generated/**")
        
        assertEquals(2, extension.includePatterns.size)
        assertEquals(listOf("**/src/**", "**/main/**"), extension.includePatterns)
        assertEquals(2, extension.excludePatterns.size)
        assertEquals(listOf("**/test/**", "**/generated/**"), extension.excludePatterns)
    }
    
    @Test
    fun `extension supports all output formats`() {
        val extension = JacocoInspectorExtension()
        
        // Test each output format
        OutputFormat.values().forEach { format ->
            extension.defaultFormat = format
            assertEquals(format, extension.defaultFormat, "Should support $format output format")
        }
    }
    
    @Test
    fun `extension can reset threshold values to null`() {
        val extension = JacocoInspectorExtension()
        
        // Set values
        extension.minClassCoverage = 80.0
        extension.maxClassCoverage = 90.0
        
        // Reset to null
        extension.minClassCoverage = null
        extension.maxClassCoverage = null
        
        assertNull(extension.minClassCoverage)
        assertNull(extension.maxClassCoverage)
    }
    
    @Test
    fun `extension can have empty pattern lists`() {
        val extension = JacocoInspectorExtension()
        
        // Set patterns
        extension.includePatterns = listOf("*.java")
        extension.excludePatterns = listOf("*.class")
        
        // Clear patterns
        extension.includePatterns = emptyList()
        extension.excludePatterns = emptyList()
        
        assertTrue(extension.includePatterns.isEmpty())
        assertTrue(extension.excludePatterns.isEmpty())
    }
}