package io.github.mpecan.jacoco.maven

import io.github.mpecan.jacoco.model.CoverageFilter
import io.github.mpecan.jacoco.model.ProjectCoverageData
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Lists project-level coverage summary
 */
@Mojo(name = "list-project", defaultPhase = LifecyclePhase.VERIFY)
class ListProjectCoverageMojo : JacocoCoverageInspectorMojo() {

    override fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any {
        // For project coverage, we return the project data itself
        // The filter doesn't apply at project level
        return coverageData
    }
}