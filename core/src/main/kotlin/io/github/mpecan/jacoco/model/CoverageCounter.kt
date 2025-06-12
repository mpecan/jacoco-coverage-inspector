package io.github.mpecan.jacoco.model

import kotlinx.serialization.Serializable

/**
 * Represents a single coverage counter from JaCoCo
 */
@Serializable
data class CoverageCounter(
    val type: CoverageType,
    val missed: Int,
    val covered: Int
) {
    val total: Int = missed + covered
    
    val ratio: Double = if (total == 0) 0.0 else covered.toDouble() / total.toDouble()
    
    val percentage: Double = ratio * 100.0
    
    fun isCoveredRatioAtLeast(threshold: Double): Boolean {
        return ratio >= threshold
    }
    
    fun isCoveredRatioAtMost(threshold: Double): Boolean {
        return ratio <= threshold
    }
    
    companion object {
        /**
         * Aggregates multiple coverage counters of the same type
         */
        fun aggregate(counters: List<CoverageCounter>): CoverageCounter {
            require(counters.isNotEmpty()) { "Cannot aggregate empty list of counters" }
            
            val type = counters.first().type
            require(counters.all { it.type == type }) { 
                "All counters must be of the same type for aggregation" 
            }
            
            val totalMissed = counters.sumOf { it.missed }
            val totalCovered = counters.sumOf { it.covered }
            
            return CoverageCounter(type, totalMissed, totalCovered)
        }
    }
}