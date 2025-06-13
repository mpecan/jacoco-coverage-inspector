package io.github.mpecan.jacoco.maven

import io.github.mpecan.jacoco.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListProjectCoverageMojoTest {

    @Test
    fun `should return project data unchanged`() {
        val mojo = ListProjectCoverageMojo()
        
        val projectData = ProjectCoverageData(
            projectName = "test-project",
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 0, covered = 10)
            ),
            packages = emptyList()
        )
        
        val filter = CoverageFilter()

        val result = mojo.generateOutput(projectData, filter)

        assertThat(result).isSameAs(projectData)
    }
}