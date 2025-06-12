package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.aggregator.ProjectCoverageAggregator
import io.github.mpecan.jacoco.model.CoverageFilter
import io.github.mpecan.jacoco.model.ProjectCoverageData

/**
 * Task to list overall project coverage
 */
abstract class ListProjectCoverageTask : BaseInspectorTask() {
    
    private val aggregator = ProjectCoverageAggregator()
    
    init {
        description = "Lists overall project coverage from JaCoCo report"
    }
    
    override fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any {
        return aggregator.aggregateProject(coverageData)
    }
}