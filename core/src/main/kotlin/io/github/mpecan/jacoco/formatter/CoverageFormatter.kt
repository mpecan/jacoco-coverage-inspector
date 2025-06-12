package io.github.mpecan.jacoco.formatter

import java.util.Locale

/**
 * Interface for formatting coverage data into different output formats
 */
interface CoverageFormatter {
    
    /**
     * Formats the given coverage data into a string representation
     * 
     * @param data The coverage data to format. Can be ProjectCoverageData, 
     *             List<PackageCoverageData>, List<ClassCoverageData>, or other data types
     * @return Formatted string representation of the data
     */
    fun format(data: Any): String
    
    companion object {
        /**
         * Formats a percentage value using US locale for consistent formatting
         * 
         * @param percentage The percentage value to format
         * @param decimals Number of decimal places (default: 2)
         * @return Formatted percentage string (e.g., "85.33")
         */
        fun formatPercentage(percentage: Double, decimals: Int = 2): String {
            return String.format(Locale.US, "%.${decimals}f", percentage)
        }
    }
}