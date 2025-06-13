package io.github.mpecan.jacoco.maven.util

import io.github.mpecan.jacoco.formatter.*
import io.github.mpecan.jacoco.model.*

/**
 * Utility object for mapping Maven plugin parameters to core model objects
 */
object MavenParameterMapper {

    /**
     * Builds a CoverageFilter from Maven plugin parameters
     */
    fun buildFilter(
        minCoverage: Double?,
        coverageType: String?,
        minClassCoverage: Double?, maxClassCoverage: Double?,
        minMethodCoverage: Double?, maxMethodCoverage: Double?,
        minLineCoverage: Double?, maxLineCoverage: Double?,
        minBranchCoverage: Double?, maxBranchCoverage: Double?,
        minInstructionCoverage: Double?, maxInstructionCoverage: Double?,
        minComplexityCoverage: Double?, maxComplexityCoverage: Double?,
        includePatterns: List<String>?, excludePatterns: List<String>?,
        packageFilter: String?
    ): CoverageFilter {
        
        val minThresholds = mutableMapOf<CoverageType, Double>()
        val maxThresholds = mutableMapOf<CoverageType, Double>()
        
        // Handle generic minCoverage option - apply to specified coverage type or default to LINE
        minCoverage?.let { coverage ->
            val targetType = coverageType?.let { 
                CoverageType.valueOf(it.uppercase()) 
            } ?: CoverageType.LINE
            minThresholds[targetType] = coverage / 100.0
        }
        
        // Apply specific coverage thresholds (these override the generic minCoverage)
        minClassCoverage?.let { minThresholds[CoverageType.CLASS] = it / 100.0 }
        maxClassCoverage?.let { maxThresholds[CoverageType.CLASS] = it / 100.0 }
        minMethodCoverage?.let { minThresholds[CoverageType.METHOD] = it / 100.0 }
        maxMethodCoverage?.let { maxThresholds[CoverageType.METHOD] = it / 100.0 }
        minLineCoverage?.let { minThresholds[CoverageType.LINE] = it / 100.0 }
        maxLineCoverage?.let { maxThresholds[CoverageType.LINE] = it / 100.0 }
        minBranchCoverage?.let { minThresholds[CoverageType.BRANCH] = it / 100.0 }
        maxBranchCoverage?.let { maxThresholds[CoverageType.BRANCH] = it / 100.0 }
        minInstructionCoverage?.let { minThresholds[CoverageType.INSTRUCTION] = it / 100.0 }
        maxInstructionCoverage?.let { maxThresholds[CoverageType.INSTRUCTION] = it / 100.0 }
        minComplexityCoverage?.let { minThresholds[CoverageType.COMPLEXITY] = it / 100.0 }
        maxComplexityCoverage?.let { maxThresholds[CoverageType.COMPLEXITY] = it / 100.0 }
        
        // Handle include patterns and package filter
        val finalIncludePatterns = mutableListOf<String>()
        includePatterns?.let { finalIncludePatterns.addAll(it) }
        packageFilter?.takeIf { it.isNotBlank() }?.let { 
            finalIncludePatterns.add("$it*") 
        }
        
        val finalExcludePatterns = excludePatterns ?: emptyList()
        
        return CoverageFilter(
            minThresholds = minThresholds,
            maxThresholds = maxThresholds,
            includePatterns = finalIncludePatterns,
            excludePatterns = finalExcludePatterns
        )
    }

    /**
     * Creates a CoverageFormatter based on the output format and color settings
     */
    fun createFormatter(format: OutputFormat, colorOutput: Boolean): CoverageFormatter {
        return when (format) {
            OutputFormat.TABLE -> TableFormatter(colorOutput, format)
            OutputFormat.JSON -> JsonFormatter()
            OutputFormat.CSV -> CsvFormatter()
            OutputFormat.MARKDOWN -> MarkdownFormatter()
        }
    }
}