package io.github.mpecan.jacoco.model

/**
 * Filter criteria for coverage reports
 */
data class CoverageFilter(
    val minThresholds: Map<CoverageType, Double> = emptyMap(),
    val maxThresholds: Map<CoverageType, Double> = emptyMap(),
    val includePatterns: List<String> = emptyList(),
    val excludePatterns: List<String> = emptyList()
) {
    fun matches(data: CoverageData): Boolean {
        // Check minimum thresholds
        for ((type, threshold) in minThresholds) {
            val counter = data.counters[type] ?: continue
            if (!counter.isCoveredRatioAtLeast(threshold)) {
                return false
            }
        }
        
        // Check maximum thresholds
        for ((type, threshold) in maxThresholds) {
            val counter = data.counters[type] ?: continue
            if (!counter.isCoveredRatioAtMost(threshold)) {
                return false
            }
        }
        
        // Check include patterns
        if (includePatterns.isNotEmpty()) {
            val matchesInclude = includePatterns.any { pattern ->
                data.name.matches(convertGlobToRegex(pattern))
            }
            if (!matchesInclude) {
                return false
            }
        }
        
        // Check exclude patterns
        if (excludePatterns.isNotEmpty()) {
            val matchesExclude = excludePatterns.any { pattern ->
                data.name.matches(convertGlobToRegex(pattern))
            }
            if (matchesExclude) {
                return false
            }
        }
        
        return true
    }
    
    private fun convertGlobToRegex(glob: String): Regex {
        val regex = glob
            .replace(".", "\\.")
            .replace("*", ".*")
            .replace("?", ".")
        return "^$regex$".toRegex()
    }
}