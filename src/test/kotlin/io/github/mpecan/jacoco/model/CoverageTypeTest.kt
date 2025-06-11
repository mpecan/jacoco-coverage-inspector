package io.github.mpecan.jacoco.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CoverageTypeTest {
    
    @Test
    fun `should have all expected types`() {
        val types = CoverageType.values()
        
        assertEquals(6, types.size)
        assertEquals(CoverageType.INSTRUCTION, types[0])
        assertEquals(CoverageType.BRANCH, types[1])
        assertEquals(CoverageType.LINE, types[2])
        assertEquals(CoverageType.COMPLEXITY, types[3])
        assertEquals(CoverageType.METHOD, types[4])
        assertEquals(CoverageType.CLASS, types[5])
    }
    
    @Test
    fun `should convert to string correctly`() {
        assertEquals("INSTRUCTION", CoverageType.INSTRUCTION.toString())
        assertEquals("BRANCH", CoverageType.BRANCH.toString())
        assertEquals("LINE", CoverageType.LINE.toString())
        assertEquals("COMPLEXITY", CoverageType.COMPLEXITY.toString())
        assertEquals("METHOD", CoverageType.METHOD.toString())
        assertEquals("CLASS", CoverageType.CLASS.toString())
    }
}