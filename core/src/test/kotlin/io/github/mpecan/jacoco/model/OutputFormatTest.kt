package io.github.mpecan.jacoco.model

import kotlin.test.Test
import kotlin.test.assertEquals

class OutputFormatTest {
    
    @Test
    fun `should have all expected formats`() {
        val formats = OutputFormat.values()
        
        assertEquals(4, formats.size)
        assertEquals(OutputFormat.TABLE, formats[0])
        assertEquals(OutputFormat.JSON, formats[1])
        assertEquals(OutputFormat.CSV, formats[2])
        assertEquals(OutputFormat.MARKDOWN, formats[3])
    }
    
    @Test
    fun `should convert to string correctly`() {
        assertEquals("TABLE", OutputFormat.TABLE.toString())
        assertEquals("JSON", OutputFormat.JSON.toString())
        assertEquals("CSV", OutputFormat.CSV.toString())
        assertEquals("MARKDOWN", OutputFormat.MARKDOWN.toString())
    }
}