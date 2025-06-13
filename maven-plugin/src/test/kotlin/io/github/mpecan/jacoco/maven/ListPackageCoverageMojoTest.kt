package io.github.mpecan.jacoco.maven

import io.github.mpecan.jacoco.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListPackageCoverageMojoTest {

    @Test
    fun `should return empty list when no packages`() {
        val mojo = ListPackageCoverageMojo()
        
        val projectData = ProjectCoverageData(
            projectName = "test-project",
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 0, covered = 0)
            ),
            packages = emptyList()
        )
        
        val filter = CoverageFilter()

        val result = mojo.generateOutput(projectData, filter) as List<*>

        assertThat(result).isEmpty()
    }

    @Test
    fun `should return all packages when filter matches all`() {
        val mojo = ListPackageCoverageMojo()
        
        val package1 = PackageCoverageData(
            packageName = "com.example.service",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 2, covered = 8)
            ),
            classes = emptyList()
        )
        
        val package2 = PackageCoverageData(
            packageName = "com.example.util",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 5, covered = 5)
            ),
            classes = emptyList()
        )
        
        val projectData = ProjectCoverageData(
            projectName = "test-project",
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 7, covered = 13)
            ),
            packages = listOf(package1, package2)
        )
        
        val filter = CoverageFilter()

        val result = mojo.generateOutput(projectData, filter) as List<PackageCoverageData>

        assertThat(result).hasSize(2)
        assertThat(result.map { it.packageName }).containsExactly(
            "com.example.service", 
            "com.example.util"
        )
    }
}