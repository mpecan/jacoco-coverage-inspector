package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.aggregator.PackageCoverageAggregator
import io.github.mpecan.jacoco.model.CoverageFilter
import io.github.mpecan.jacoco.model.ProjectCoverageData

/**
 * Task to list package-level coverage with filtering
 */
abstract class ListPackageCoverageTask : BaseInspectorTask() {
    
    private val aggregator = PackageCoverageAggregator()
    
    init {
        description = "Lists package-level coverage from JaCoCo report with filtering capabilities"
    }
    
    override fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any {
        return aggregator.aggregatePackages(coverageData, filter)
    }
}