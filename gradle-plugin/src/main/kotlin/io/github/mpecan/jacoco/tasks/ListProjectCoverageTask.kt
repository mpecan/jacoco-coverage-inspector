package io.github.mpecan.jacoco.tasks

/**
 * Task to list overall project coverage
 */
abstract class ListProjectCoverageTask : BaseInspectorTask() {
    
    init {
        description = "Lists overall project coverage from JaCoCo report"
    }
}