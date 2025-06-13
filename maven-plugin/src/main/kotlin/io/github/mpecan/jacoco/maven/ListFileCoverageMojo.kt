package io.github.mpecan.jacoco.maven

import io.github.mpecan.jacoco.model.*
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Lists file-level coverage details with filtering options
 */
@Mojo(name = "list-files", defaultPhase = LifecyclePhase.VERIFY)
class ListFileCoverageMojo : JacocoCoverageInspectorMojo() {

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