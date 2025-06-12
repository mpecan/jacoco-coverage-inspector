package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.model.ClassCoverageData
import io.github.mpecan.jacoco.model.CoverageFilter
import io.github.mpecan.jacoco.model.ProjectCoverageData

/**
 * Task to list file-level coverage with filtering
 */
abstract class ListFileCoverageTask : BaseInspectorTask() {
    
    init {
        description = "Lists file-level coverage from JaCoCo report with filtering capabilities"
    }
    
    override fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any {
        val allClasses = mutableListOf<ClassCoverageData>()
        
        // Collect all classes from all packages
        for (pkg in coverageData.packages) {
            for (cls in pkg.classes) {
                // Apply filter to each class
                if (filter.matches(cls)) {
                    allClasses.add(cls)
                }
            }
        }
        
        // Sort by class name for consistent output
        return allClasses.sortedBy { it.className }
    }
}