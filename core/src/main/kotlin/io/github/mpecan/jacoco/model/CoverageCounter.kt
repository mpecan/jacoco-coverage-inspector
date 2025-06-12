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
}