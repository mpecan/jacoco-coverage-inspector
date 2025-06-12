package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.formatter.*
import io.github.mpecan.jacoco.model.OutputFormat
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FormatterFactoryTest {
    
    private val factory = FormatterFactory()
    
    @Test
    fun `creates TableFormatter for TABLE format`() {
        val formatter = factory.createFormatter(OutputFormat.TABLE, colorOutput = true)
        assertTrue(formatter is TableFormatter)
    }
    
    @Test
    fun `creates TableFormatter for TABLE format with color options`() {
        val formatterWithColor = factory.createFormatter(OutputFormat.TABLE, colorOutput = true)
        val formatterWithoutColor = factory.createFormatter(OutputFormat.TABLE, colorOutput = false)
        
        assertTrue(formatterWithColor is TableFormatter)
        assertTrue(formatterWithoutColor is TableFormatter)
        // Note: We'd need to expose colorOutput property to test this further
    }
    
    @Test
    fun `creates JsonFormatter for JSON format`() {
        val formatter = factory.createFormatter(OutputFormat.JSON, colorOutput = false)
        assertTrue(formatter is JsonFormatter)
    }
    
    @Test
    fun `creates CsvFormatter for CSV format`() {
        val formatter = factory.createFormatter(OutputFormat.CSV, colorOutput = false)
        assertTrue(formatter is CsvFormatter)
    }
    
    @Test
    fun `creates MarkdownFormatter for MARKDOWN format`() {
        val formatter = factory.createFormatter(OutputFormat.MARKDOWN, colorOutput = false)
        assertTrue(formatter is MarkdownFormatter)
    }
    
    @Test
    fun `color output setting does not affect non-table formatters`() {
        // These should work regardless of colorOutput setting
        assertTrue(factory.createFormatter(OutputFormat.JSON, true) is JsonFormatter)
        assertTrue(factory.createFormatter(OutputFormat.JSON, false) is JsonFormatter)
        assertTrue(factory.createFormatter(OutputFormat.CSV, true) is CsvFormatter)
        assertTrue(factory.createFormatter(OutputFormat.CSV, false) is CsvFormatter)
        assertTrue(factory.createFormatter(OutputFormat.MARKDOWN, true) is MarkdownFormatter)
        assertTrue(factory.createFormatter(OutputFormat.MARKDOWN, false) is MarkdownFormatter)
    }
}