package io.github.mpecan.jacoco.aggregator

import io.github.mpecan.jacoco.model.CoverageCounter
import io.github.mpecan.jacoco.model.CoverageType
import kotlinx.serialization.Serializable

/**
 * Aggregated coverage data for project-level reporting
 */
@Serializable
data class AggregatedProjectCoverage(
    val projectName: String,
    val totalCounters: Map<CoverageType, CoverageCounter>,
    val packageCount: Int,
    val classCount: Int,
    val methodCount: Int
)

/**
 * Aggregated coverage data for package-level reporting
 */
@Serializable
data class AggregatedPackageCoverage(
    val packageName: String,
    val aggregatedCounters: Map<CoverageType, CoverageCounter>,
    val classCount: Int,
    val methodCount: Int
)