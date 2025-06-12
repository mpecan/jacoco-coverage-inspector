package io.github.mpecan.jacoco

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Execution(ExecutionMode.CONCURRENT)
class CoverageFixtureTest : PluginFixtureTest() {

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class ProjectCoverageOutputFormatTests {

        @Test
        fun `listProjectCoverage works with default table format`() {
            loadFixture("simple-project")

            val result = runGradle("test", "jacocoTestReport", "listProjectCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.contains("PROJECT COVERAGE SUMMARY"))
        }

        @Test
        fun `listProjectCoverage works with JSON format`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=json")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.contains("\"projectName\""))
        }

        @Test
        fun `listProjectCoverage works with CSV format`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=csv")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.contains("PROJECT,"))
        }

        @Test
        fun `listProjectCoverage works with Markdown format`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=markdown")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.contains("**Project:**"))
        }

        @Test
        fun `listProjectCoverage table format produces valid content`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=table")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "Table format should produce output")
        }

        @Test
        fun `listProjectCoverage json format produces valid content`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=json")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "JSON format should produce output")
        }

        @Test
        fun `listProjectCoverage csv format produces valid content`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=csv")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "CSV format should produce output")
        }

        @Test
        fun `listProjectCoverage markdown format produces valid content`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=markdown")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "Markdown format should produce output")
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class FileCoverageFilteringTests {

        @Test
        fun `listFileCoverage works with default settings`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `listFileCoverage works with minimum coverage filter`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minCoverage=50")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `listFileCoverage works with coverage type filter`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--coverageType=INSTRUCTION")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `listFileCoverage works with package filter`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--packageFilter=com.test")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class PackageCoverageTests {

        @Test
        fun `listPackageCoverage works with default table format`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listPackageCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listPackageCoverage")?.outcome)
            assertTrue(result.output.contains("PACKAGE COVERAGE"))
        }

        @Test
        fun `listPackageCoverage works with JSON format`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listPackageCoverage", "--format=json")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listPackageCoverage")?.outcome)
            assertTrue(result.output.contains("\"packageName\""))
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class SpecialProjectTests {

        @Test
        fun `plugin works with low coverage project - project coverage`() {
            loadFixture("low-coverage-project")

            val result = runGradle("test", "jacocoTestReport", "listProjectCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        }

        @Test
        fun `plugin works with low coverage project - file coverage`() {
            loadFixture("low-coverage-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minCoverage=0")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
            assertTrue(result.output.contains("Calculator"))
        }

        @Test
        fun `plugin works with zero coverage project - project coverage`() {
            loadFixture("zero-coverage-project")

            val result = runGradle("test", "jacocoTestReport", "listProjectCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        }

        @Test
        fun `plugin works with zero coverage project - file coverage`() {
            loadFixture("zero-coverage-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
            assertTrue(
                result.output.contains("No items match the specified filters.") || result.output.contains(
                    "0.00%"
                )
            )
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class EdgeCaseTests {

        @Test
        fun `listProjectCoverage handles missing jacoco report gracefully`() {
            loadFixture("simple-project")

            val result = runGradle("listProjectCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        }

        @Test
        fun `listFileCoverage handles missing jacoco report gracefully`() {
            loadFixture("simple-project")

            val result = runGradle("listFileCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `listPackageCoverage handles missing jacoco report gracefully`() {
            loadFixture("simple-project")

            val result = runGradle("listPackageCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listPackageCoverage")?.outcome)
        }
    }
}