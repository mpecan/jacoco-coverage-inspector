package io.github.mpecan.jacoco

import io.github.mpecan.jacoco.model.OutputFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JacocoInspectorExtensionTest {
    
    @Test
    fun `should have default values`() {
        val extension = JacocoInspectorExtension()
        
        assertEquals(OutputFormat.TABLE, extension.defaultFormat)
        assertTrue(extension.colorOutput)
        assertEquals(emptyList(), extension.excludePatterns)
        assertEquals(emptyList(), extension.includePatterns)
    }
    
    @Test
    fun `should allow setting all minimum coverage thresholds`() {
        val extension = JacocoInspectorExtension()
        
        extension.minClassCoverage = 0.8
        extension.minMethodCoverage = 0.75
        extension.minLineCoverage = 0.85
        extension.minBranchCoverage = 0.7
        extension.minInstructionCoverage = 0.9
        extension.minComplexityCoverage = 0.65
        
        assertEquals(0.8, extension.minClassCoverage)
        assertEquals(0.75, extension.minMethodCoverage)
        assertEquals(0.85, extension.minLineCoverage)
        assertEquals(0.7, extension.minBranchCoverage)
        assertEquals(0.9, extension.minInstructionCoverage)
        assertEquals(0.65, extension.minComplexityCoverage)
    }
    
    @Test
    fun `should allow setting all maximum coverage thresholds`() {
        val extension = JacocoInspectorExtension()
        
        extension.maxClassCoverage = 0.8
        extension.maxMethodCoverage = 0.75
        extension.maxLineCoverage = 0.85
        extension.maxBranchCoverage = 0.7
        extension.maxInstructionCoverage = 0.9
        extension.maxComplexityCoverage = 0.65
        
        assertEquals(0.8, extension.maxClassCoverage)
        assertEquals(0.75, extension.maxMethodCoverage)
        assertEquals(0.85, extension.maxLineCoverage)
        assertEquals(0.7, extension.maxBranchCoverage)
        assertEquals(0.9, extension.maxInstructionCoverage)
        assertEquals(0.65, extension.maxComplexityCoverage)
    }
    
    @Test
    fun `should allow setting patterns`() {
        val extension = JacocoInspectorExtension()
        
        extension.excludePatterns = listOf("*Test*", "*Mock*")
        extension.includePatterns = listOf("com.example.*")
        
        assertEquals(listOf("*Test*", "*Mock*"), extension.excludePatterns)
        assertEquals(listOf("com.example.*"), extension.includePatterns)
    }
    
    @Test
    fun `should allow setting output format and color`() {
        val extension = JacocoInspectorExtension()
        
        extension.defaultFormat = OutputFormat.JSON
        extension.colorOutput = false
        
        assertEquals(OutputFormat.JSON, extension.defaultFormat)
        assertEquals(false, extension.colorOutput)
    }
}