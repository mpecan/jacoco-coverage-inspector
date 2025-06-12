package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.model.CoverageFilter
import io.github.mpecan.jacoco.model.PackageCoverageData
import io.github.mpecan.jacoco.model.ProjectCoverageData

/**
 * Task to list package-level coverage with filtering
 */
abstract class ListPackageCoverageTask : BaseInspectorTask() {
    
    init {
        description = "Lists package-level coverage from JaCoCo report with filtering capabilities"
    }
    
    override fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any {
        val filteredPackages = mutableListOf<PackageCoverageData>()
        
        // Apply filter to each package
        for (pkg in coverageData.packages) {
            if (filter.matches(pkg)) {
                filteredPackages.add(pkg)
            }
        }
        
        // Sort by package name for consistent output
        return filteredPackages.sortedBy { it.packageName }
    }
}