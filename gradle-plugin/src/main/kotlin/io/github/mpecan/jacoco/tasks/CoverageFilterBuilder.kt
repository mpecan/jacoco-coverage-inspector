package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.model.CoverageFilter
import io.github.mpecan.jacoco.model.CoverageType

/**
 * Builder for creating CoverageFilter instances from task options
 */
class CoverageFilterBuilder {
    
    fun buildFilter(
        minCoverage: Double? = null,
        coverageType: CoverageType? = null,
        minClassCoverage: Double? = null,
        minMethodCoverage: Double? = null,
        minLineCoverage: Double? = null,
        minBranchCoverage: Double? = null,
        minInstructionCoverage: Double? = null,
        minComplexityCoverage: Double? = null,
        maxClassCoverage: Double? = null,
        maxMethodCoverage: Double? = null,
        maxLineCoverage: Double? = null,
        maxBranchCoverage: Double? = null,
        maxInstructionCoverage: Double? = null,
        maxComplexityCoverage: Double? = null,
        includePatterns: List<String> = emptyList(),
        excludePatterns: List<String> = emptyList(),
        packageFilter: String? = null
    ): CoverageFilter {
        val minThresholds = mutableMapOf<CoverageType, Double>()
        val maxThresholds = mutableMapOf<CoverageType, Double>()

        // Handle generic minCoverage option - apply to specified coverage type or default to LINE
        minCoverage?.let { coverage ->
            val targetType = coverageType ?: CoverageType.LINE
            minThresholds[targetType] = coverage / 100.0
        }

        // Build min thresholds from specific options (these override the generic minCoverage)
        minClassCoverage?.let { minThresholds[CoverageType.CLASS] = it / 100.0 }
        minMethodCoverage?.let { minThresholds[CoverageType.METHOD] = it / 100.0 }
        minLineCoverage?.let { minThresholds[CoverageType.LINE] = it / 100.0 }
        minBranchCoverage?.let { minThresholds[CoverageType.BRANCH] = it / 100.0 }
        minInstructionCoverage?.let { minThresholds[CoverageType.INSTRUCTION] = it / 100.0 }
        minComplexityCoverage?.let { minThresholds[CoverageType.COMPLEXITY] = it / 100.0 }

        // Build max thresholds
        maxClassCoverage?.let { maxThresholds[CoverageType.CLASS] = it / 100.0 }
        maxMethodCoverage?.let { maxThresholds[CoverageType.METHOD] = it / 100.0 }
        maxLineCoverage?.let { maxThresholds[CoverageType.LINE] = it / 100.0 }
        maxBranchCoverage?.let { maxThresholds[CoverageType.BRANCH] = it / 100.0 }
        maxInstructionCoverage?.let { maxThresholds[CoverageType.INSTRUCTION] = it / 100.0 }
        maxComplexityCoverage?.let { maxThresholds[CoverageType.COMPLEXITY] = it / 100.0 }

        // Handle package filter - add it to include patterns
        val includePatternsList = includePatterns.toMutableList()
        packageFilter?.let { pattern ->
            includePatternsList.add("$pattern*")
        }

        return CoverageFilter(
            minThresholds = minThresholds,
            maxThresholds = maxThresholds,
            includePatterns = includePatternsList,
            excludePatterns = excludePatterns
        )
    }
}