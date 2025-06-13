package io.github.mpecan.jacoco.tasks

/**
 * Data class holding filter patterns for coverage analysis
 */
data class FilterPatterns(
    val includePatterns: List<String> = emptyList(),
    val excludePatterns: List<String> = emptyList(),
    val packageFilter: String? = null
)