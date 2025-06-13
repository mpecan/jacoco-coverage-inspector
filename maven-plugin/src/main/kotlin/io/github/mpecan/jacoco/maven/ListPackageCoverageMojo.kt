package io.github.mpecan.jacoco.maven

import io.github.mpecan.jacoco.model.*
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Lists package-level aggregated coverage data
 */
@Mojo(name = "list-packages", defaultPhase = LifecyclePhase.VERIFY)
class ListPackageCoverageMojo : JacocoCoverageInspectorMojo() {

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