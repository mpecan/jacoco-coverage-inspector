package io.github.mpecan.jacoco.maven

import io.github.mpecan.jacoco.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListFileCoverageMojoTest {

    @Test
    fun `should return empty list when no packages`() {
        val mojo = ListFileCoverageMojo()
        
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
    fun `should return classes from packages`() {
        val mojo = ListFileCoverageMojo()
        
        val classData = ClassCoverageData(
            className = "com.example.TestClass",
            classCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 2, covered = 8)
            ),
            sourceFileName = "TestClass.java"
        )
        
        val packageData = PackageCoverageData(
            packageName = "com.example",
            packageCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 2, covered = 8)
            ),
            classes = listOf(classData)
        )
        
        val projectData = ProjectCoverageData(
            projectName = "test-project",
            projectCounters = mapOf(
                CoverageType.LINE to CoverageCounter(CoverageType.LINE, missed = 2, covered = 8)
            ),
            packages = listOf(packageData)
        )
        
        val filter = CoverageFilter()

        val result = mojo.generateOutput(projectData, filter) as List<ClassCoverageData>

        assertThat(result).hasSize(1)
        assertThat(result.first().className).isEqualTo("com.example.TestClass")
    }
}