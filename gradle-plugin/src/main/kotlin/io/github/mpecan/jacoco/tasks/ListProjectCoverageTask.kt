package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.model.CoverageFilter
import io.github.mpecan.jacoco.model.ProjectCoverageData
import org.gradle.api.tasks.TaskAction

/**
 * Task to list overall project coverage
 */
abstract class ListProjectCoverageTask : BaseInspectorTask() {
    
    init {
        description = "Lists overall project coverage from JaCoCo report"
    }
    
    override fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any {
        // For project coverage, we just return the project data itself
        // The filter doesn't apply at project level
        return coverageData
    }
}