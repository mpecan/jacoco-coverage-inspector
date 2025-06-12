package io.github.mpecan.jacoco.aggregator

import io.github.mpecan.jacoco.model.*

/**
 * Aggregates coverage data at the project level
 */
class ProjectCoverageAggregator {
    
    /**
     * Aggregates coverage data for the entire project
     */
    fun aggregateProject(projectData: ProjectCoverageData): AggregatedProjectCoverage {
        val aggregatedCounters = mutableMapOf<CoverageType, CoverageCounter>()
        
        // Sum up counters from all packages
        for (coverageType in CoverageType.entries) {
            val projectCounter = projectData.getCounter(coverageType)
            if (projectCounter != null) {
                aggregatedCounters[coverageType] = projectCounter
            }
        }
        
        // Count totals
        val packageCount = projectData.packages.size
        val classCount = projectData.packages.sumOf { it.classes.size }
        val methodCount = projectData.packages.sumOf { pkg ->
            pkg.classes.sumOf { cls -> cls.methods.size }
        }
        
        return AggregatedProjectCoverage(
            projectName = projectData.projectName,
            totalCounters = aggregatedCounters,
            packageCount = packageCount,
            classCount = classCount,
            methodCount = methodCount
        )
    }
    
    /**
     * Aggregates coverage data from multiple projects (for multi-project builds)
     */
    fun aggregateMultipleProjects(projects: List<ProjectCoverageData>): AggregatedProjectCoverage {
        if (projects.isEmpty()) {
            return AggregatedProjectCoverage(
                projectName = "No Projects",
                totalCounters = emptyMap(),
                packageCount = 0,
                classCount = 0,
                methodCount = 0
            )
        }
        
        if (projects.size == 1) {
            return aggregateProject(projects.first())
        }
        
        // Aggregate across multiple projects
        val aggregatedCounters = mutableMapOf<CoverageType, CoverageCounter>()
        
        for (coverageType in CoverageType.entries) {
            val counters = projects.mapNotNull { it.getCounter(coverageType) }
            if (counters.isNotEmpty()) {
                aggregatedCounters[coverageType] = CoverageCounter.aggregate(counters)
            }
        }
        
        val totalPackageCount = projects.sumOf { it.packages.size }
        val totalClassCount = projects.sumOf { project ->
            project.packages.sumOf { it.classes.size }
        }
        val totalMethodCount = projects.sumOf { project ->
            project.packages.sumOf { pkg ->
                pkg.classes.sumOf { cls -> cls.methods.size }
            }
        }
        
        return AggregatedProjectCoverage(
            projectName = "Multi-Project (${projects.size} projects)",
            totalCounters = aggregatedCounters,
            packageCount = totalPackageCount,
            classCount = totalClassCount,
            methodCount = totalMethodCount
        )
    }
}