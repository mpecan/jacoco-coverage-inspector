package io.github.mpecan.jacoco.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoverageCounterTest {
    
    @Test
    fun `should calculate total correctly`() {
        val counter = CoverageCounter(CoverageType.LINE, missed = 20, covered = 80)
        assertEquals(100, counter.total)
    }
    
    @Test
    fun `should calculate ratio correctly`() {
        val counter = CoverageCounter(CoverageType.LINE, missed = 20, covered = 80)
        assertEquals(0.8, counter.ratio, 0.001)
    }
    
    @Test
    fun `should calculate percentage correctly`() {
        val counter = CoverageCounter(CoverageType.LINE, missed = 20, covered = 80)
        assertEquals(80.0, counter.percentage, 0.001)
    }
    
    @Test
    fun `should handle zero coverage`() {
        val counter = CoverageCounter(CoverageType.LINE, missed = 100, covered = 0)
        assertEquals(0.0, counter.ratio)
        assertEquals(0.0, counter.percentage)
    }
    
    @Test
    fun `should handle full coverage`() {
        val counter = CoverageCounter(CoverageType.LINE, missed = 0, covered = 100)
        assertEquals(1.0, counter.ratio)
        assertEquals(100.0, counter.percentage)
    }
    
    @Test
    fun `should handle zero total gracefully`() {
        val counter = CoverageCounter(CoverageType.LINE, missed = 0, covered = 0)
        assertEquals(0.0, counter.ratio)
        assertEquals(0.0, counter.percentage)
    }
    
    @Test
    fun `should correctly check minimum thresholds`() {
        val counter = CoverageCounter(CoverageType.LINE, missed = 20, covered = 80)
        
        assertTrue(counter.isCoveredRatioAtLeast(0.8))
        assertTrue(counter.isCoveredRatioAtLeast(0.79))
        assertFalse(counter.isCoveredRatioAtLeast(0.81))
    }
    
    @Test
    fun `should correctly check maximum thresholds`() {
        val counter = CoverageCounter(CoverageType.LINE, missed = 20, covered = 80)
        
        assertTrue(counter.isCoveredRatioAtMost(0.8))
        assertTrue(counter.isCoveredRatioAtMost(0.81))
        assertFalse(counter.isCoveredRatioAtMost(0.79))
    }
}