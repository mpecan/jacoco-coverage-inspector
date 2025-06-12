package io.github.mpecan.jacoco.tasks

/**
 * Task to list file-level coverage with filtering
 */
abstract class ListFileCoverageTask : BaseInspectorTask() {
    
    init {
        description = "Lists file-level coverage from JaCoCo report with filtering capabilities"
    }
}