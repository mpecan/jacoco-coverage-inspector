package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.formatter.*
import io.github.mpecan.jacoco.model.OutputFormat

/**
 * Factory for creating CoverageFormatter instances
 */
class FormatterFactory {
    
    fun createFormatter(format: OutputFormat, colorOutput: Boolean): CoverageFormatter {
        return when (format) {
            OutputFormat.TABLE -> TableFormatter(
                colorOutput = colorOutput,
                outputFormat = format
            )
            OutputFormat.JSON -> JsonFormatter()
            OutputFormat.CSV -> CsvFormatter()
            OutputFormat.MARKDOWN -> MarkdownFormatter()
        }
    }
}