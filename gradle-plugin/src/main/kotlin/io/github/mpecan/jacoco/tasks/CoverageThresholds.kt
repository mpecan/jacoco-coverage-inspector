package io.github.mpecan.jacoco.tasks

/**
 * Data class holding coverage threshold values for different coverage types
 */
data class CoverageThresholds(
    val minClass: Double? = null,
    val maxClass: Double? = null,
    val minMethod: Double? = null,
    val maxMethod: Double? = null,
    val minLine: Double? = null,
    val maxLine: Double? = null,
    val minBranch: Double? = null,
    val maxBranch: Double? = null,
    val minInstruction: Double? = null,
    val maxInstruction: Double? = null,
    val minComplexity: Double? = null,
    val maxComplexity: Double? = null
)