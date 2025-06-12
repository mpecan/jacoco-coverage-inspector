package io.github.mpecan.jacoco.aggregator

import io.github.mpecan.jacoco.model.*

/**
 * Aggregates coverage data at the package level
 */
class PackageCoverageAggregator {
    
    /**
     * Aggregates coverage data for packages with optional filtering
     */
    fun aggregatePackages(
        projectData: ProjectCoverageData,
        filter: CoverageFilter? = null
    ): List<AggregatedPackageCoverage> {
        return projectData.packages
            .filter { pkg -> filter?.matches(pkg) ?: true }
            .map { pkg -> aggregatePackage(pkg) }
            .sortedBy { it.packageName }
    }
    
    /**
     * Aggregates coverage data for a single package
     */
    fun aggregatePackage(packageData: PackageCoverageData): AggregatedPackageCoverage {
        val aggregatedCounters = mutableMapOf<CoverageType, CoverageCounter>()
        
        // Use the package-level counters (these should already be aggregated)
        for (coverageType in CoverageType.entries) {
            val packageCounter = packageData.getCounter(coverageType)
            if (packageCounter != null) {
                aggregatedCounters[coverageType] = packageCounter
            }
        }
        
        // Count totals
        val classCount = packageData.classes.size
        val methodCount = packageData.classes.sumOf { it.methods.size }
        
        return AggregatedPackageCoverage(
            packageName = packageData.packageName,
            aggregatedCounters = aggregatedCounters,
            classCount = classCount,
            methodCount = methodCount
        )
    }
    
    /**
     * Aggregates coverage data from multiple packages with the same name
     * (useful for multi-project builds where the same package exists in multiple projects)
     */
    fun aggregatePackagesByName(packages: List<PackageCoverageData>): List<AggregatedPackageCoverage> {
        if (packages.isEmpty()) {
            return emptyList()
        }
        
        // Group packages by name
        val packageGroups = packages.groupBy { it.packageName }
        
        return packageGroups.map { (packageName, packagesWithSameName) ->
            if (packagesWithSameName.size == 1) {
                aggregatePackage(packagesWithSameName.first())
            } else {
                aggregateMultiplePackages(packageName, packagesWithSameName)
            }
        }.sortedBy { it.packageName }
    }
    
    /**
     * Aggregates multiple packages with the same name
     */
    private fun aggregateMultiplePackages(
        packageName: String,
        packages: List<PackageCoverageData>
    ): AggregatedPackageCoverage {
        val aggregatedCounters = mutableMapOf<CoverageType, CoverageCounter>()
        
        for (coverageType in CoverageType.entries) {
            val counters = packages.mapNotNull { it.getCounter(coverageType) }
            if (counters.isNotEmpty()) {
                aggregatedCounters[coverageType] = CoverageCounter.aggregate(counters)
            }
        }
        
        val totalClassCount = packages.sumOf { it.classes.size }
        val totalMethodCount = packages.sumOf { pkg ->
            pkg.classes.sumOf { cls -> cls.methods.size }
        }
        
        return AggregatedPackageCoverage(
            packageName = packageName,
            aggregatedCounters = aggregatedCounters,
            classCount = totalClassCount,
            methodCount = totalMethodCount
        )
    }
}