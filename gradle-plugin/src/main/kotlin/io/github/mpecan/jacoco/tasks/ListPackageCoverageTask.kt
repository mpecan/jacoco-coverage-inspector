package io.github.mpecan.jacoco.tasks

/**
 * Task to list package-level coverage with filtering
 */
abstract class ListPackageCoverageTask : BaseInspectorTask() {
    
    init {
        description = "Lists package-level coverage from JaCoCo report with filtering capabilities"
    }
}