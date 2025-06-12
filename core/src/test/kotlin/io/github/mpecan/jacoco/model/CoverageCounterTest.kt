package io.github.mpecan.jacoco.model

import kotlin.test.*

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
    
    @Test
    fun `should aggregate counters of same type`() {
        val counter1 = CoverageCounter(CoverageType.LINE, missed = 20, covered = 80)
        val counter2 = CoverageCounter(CoverageType.LINE, missed = 30, covered = 70)
        val counter3 = CoverageCounter(CoverageType.LINE, missed = 10, covered = 40)
        
        val aggregated = CoverageCounter.aggregate(listOf(counter1, counter2, counter3))
        
        assertEquals(CoverageType.LINE, aggregated.type)
        assertEquals(60, aggregated.missed) // 20 + 30 + 10
        assertEquals(190, aggregated.covered) // 80 + 70 + 40
        assertEquals(250, aggregated.total) // 60 + 190
        assertEquals(0.76, aggregated.ratio, 0.001) // 190 / 250
        assertEquals(76.0, aggregated.percentage, 0.001)
    }
    
    @Test
    fun `should aggregate single counter`() {
        val counter = CoverageCounter(CoverageType.BRANCH, missed = 5, covered = 15)
        val aggregated = CoverageCounter.aggregate(listOf(counter))
        
        assertEquals(counter.type, aggregated.type)
        assertEquals(counter.missed, aggregated.missed)
        assertEquals(counter.covered, aggregated.covered)
        assertEquals(counter.total, aggregated.total)
        assertEquals(counter.ratio, aggregated.ratio)
    }
    
    @Test
    fun `should handle aggregation of all zero counters`() {
        val counter1 = CoverageCounter(CoverageType.METHOD, missed = 0, covered = 0)
        val counter2 = CoverageCounter(CoverageType.METHOD, missed = 0, covered = 0)
        
        val aggregated = CoverageCounter.aggregate(listOf(counter1, counter2))
        
        assertEquals(CoverageType.METHOD, aggregated.type)
        assertEquals(0, aggregated.missed)
        assertEquals(0, aggregated.covered)
        assertEquals(0, aggregated.total)
        assertEquals(0.0, aggregated.ratio)
        assertEquals(0.0, aggregated.percentage)
    }
    
    @Test
    fun `should throw exception when aggregating empty list`() {
        assertFailsWith<IllegalArgumentException> {
            CoverageCounter.aggregate(emptyList())
        }
    }
    
    @Test
    fun `should throw exception when aggregating different types`() {
        val counter1 = CoverageCounter(CoverageType.LINE, missed = 10, covered = 20)
        val counter2 = CoverageCounter(CoverageType.BRANCH, missed = 5, covered = 10)
        
        assertFailsWith<IllegalArgumentException> {
            CoverageCounter.aggregate(listOf(counter1, counter2))
        }
    }
}