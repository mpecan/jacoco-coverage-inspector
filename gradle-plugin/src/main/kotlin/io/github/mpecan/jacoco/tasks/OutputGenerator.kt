package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.model.*

/**
 * Generates output for different coverage tasks
 */
class OutputGenerator {
    
    fun generateProjectOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): ProjectCoverageData {
        // For project coverage, we just return the project data itself
        // The filter doesn't apply at project level
        return coverageData
    }
    
    fun generateFileOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): List<ClassCoverageData> {
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
    
    fun generatePackageOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): List<PackageCoverageData> {
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