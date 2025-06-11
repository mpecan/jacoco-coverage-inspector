package io.github.mpecan.jacoco.model

/**
 * Supported output formats for coverage reports
 */
enum class OutputFormat {
    /**
     * Human-readable table format with optional color coding
     */
    TABLE,
    
    /**
     * JSON format for programmatic consumption
     */
    JSON,
    
    /**
     * CSV format for spreadsheet analysis
     */
    CSV,
    
    /**
     * Markdown format for documentation
     */
    MARKDOWN
}