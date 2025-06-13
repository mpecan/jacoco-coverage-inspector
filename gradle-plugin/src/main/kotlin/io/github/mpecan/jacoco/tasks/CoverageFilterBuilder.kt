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
        thresholds: CoverageThresholds = CoverageThresholds(),
        filterPatterns: FilterPatterns = FilterPatterns()
    ): CoverageFilter {
        val minThresholds = mutableMapOf<CoverageType, Double>()
        val maxThresholds = mutableMapOf<CoverageType, Double>()

        // Handle generic minCoverage option - apply to specified coverage type or default to LINE
        minCoverage?.let { coverage ->
            val targetType = coverageType ?: CoverageType.LINE
            minThresholds[targetType] = coverage / 100.0
        }

        // Build min thresholds from specific options (these override the generic minCoverage)
        thresholds.minClass?.let { minThresholds[CoverageType.CLASS] = it / 100.0 }
        thresholds.minMethod?.let { minThresholds[CoverageType.METHOD] = it / 100.0 }
        thresholds.minLine?.let { minThresholds[CoverageType.LINE] = it / 100.0 }
        thresholds.minBranch?.let { minThresholds[CoverageType.BRANCH] = it / 100.0 }
        thresholds.minInstruction?.let { minThresholds[CoverageType.INSTRUCTION] = it / 100.0 }
        thresholds.minComplexity?.let { minThresholds[CoverageType.COMPLEXITY] = it / 100.0 }

        // Build max thresholds
        thresholds.maxClass?.let { maxThresholds[CoverageType.CLASS] = it / 100.0 }
        thresholds.maxMethod?.let { maxThresholds[CoverageType.METHOD] = it / 100.0 }
        thresholds.maxLine?.let { maxThresholds[CoverageType.LINE] = it / 100.0 }
        thresholds.maxBranch?.let { maxThresholds[CoverageType.BRANCH] = it / 100.0 }
        thresholds.maxInstruction?.let { maxThresholds[CoverageType.INSTRUCTION] = it / 100.0 }
        thresholds.maxComplexity?.let { maxThresholds[CoverageType.COMPLEXITY] = it / 100.0 }

        // Handle package filter - add it to include patterns
        val includePatternsList = filterPatterns.includePatterns.toMutableList()
        filterPatterns.packageFilter?.let { pattern ->
            includePatternsList.add("$pattern*")
        }

        return CoverageFilter(
            minThresholds = minThresholds,
            maxThresholds = maxThresholds,
            includePatterns = includePatternsList,
            excludePatterns = filterPatterns.excludePatterns
        )
    }
}