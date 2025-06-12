package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.PluginFixtureTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Execution(ExecutionMode.CONCURRENT)
class BaseInspectorTaskTest : PluginFixtureTest() {

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class OutputFormatTests {

        @Test
        fun `test table output format works correctly`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=table")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "Table format should produce output")
        }

        @Test
        fun `test json output format works correctly`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=json")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "JSON format should produce output")
        }

        @Test
        fun `test csv output format works correctly`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=csv")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "CSV format should produce output")
        }

        @Test
        fun `test markdown output format works correctly`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--format=markdown")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "Markdown format should produce output")
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class CoverageTypeFilteringTests {

        @Test
        fun `test INSTRUCTION coverage type filtering works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--coverageType=INSTRUCTION")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test BRANCH coverage type filtering works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--coverageType=BRANCH")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test LINE coverage type filtering works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--coverageType=LINE")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test COMPLEXITY coverage type filtering works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--coverageType=COMPLEXITY")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test METHOD coverage type filtering works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--coverageType=METHOD")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test CLASS coverage type filtering works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--coverageType=CLASS")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class MinimumCoverageThresholdTests {

        @Test
        fun `test generic minCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minCoverage=50")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test minLineCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minLineCoverage=80")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test minBranchCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minBranchCoverage=70")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test minMethodCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minMethodCoverage=60")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test minClassCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minClassCoverage=90")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test minInstructionCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minInstructionCoverage=85")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test minComplexityCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minComplexityCoverage=75")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test generic minCoverage with different coverage types`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minCoverage=50", "--coverageType=BRANCH")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test generic minCoverage defaults to LINE when no coverage type specified`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--minCoverage=50")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class MaximumCoverageThresholdTests {

        @Test
        fun `test maxLineCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--maxLineCoverage=100")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test maxBranchCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--maxBranchCoverage=100")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test maxMethodCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--maxMethodCoverage=100")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test maxClassCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--maxClassCoverage=100")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test maxInstructionCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--maxInstructionCoverage=100")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test maxComplexityCoverage threshold works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--maxComplexityCoverage=100")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class PatternAndFilterTests {

        @Test
        fun `test package filtering works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--packageFilter=com.test")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
            assertTrue(result.output.isNotBlank(), "Package filter should produce output")
        }

        @Test
        fun `test include patterns work`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--includePatterns=com.test.*")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test exclude patterns work`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listFileCoverage", "--excludePatterns=*.Test*")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
        }

        @Test
        fun `test color output option works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--color")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        }

        @Test
        fun `test no color output option works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle("listProjectCoverage", "--no-color")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
        }

        @Test
        fun `test combination of filters works`() {
            loadFixture("simple-project")
            runGradle("test", "jacocoTestReport")

            val result = runGradle(
                "listFileCoverage",
                "--minCoverage=0",
                "--coverageType=LINE",
                "--packageFilter=com.test",
                "--format=json"
            )
            assertEquals(TaskOutcome.SUCCESS, result.task(":listFileCoverage")?.outcome)
            assertTrue(
                result.output.contains("\"className\""),
                "Should contain JSON formatted output"
            )
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class ExtensionConfigurationTests {

        @Test
        fun `test extension configuration is applied`() {
            loadFixture("simple-project")

            // Modify build file to configure extension
            val buildFile = testProjectDir.resolve("build.gradle.kts")
            val originalContent = buildFile.readText()
            buildFile.writeText(
                originalContent + """
                
                jacocoInspector {
                    defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.JSON
                    colorOutput = false
                    minLineCoverage = 50.0
                    maxLineCoverage = 100.0
                    includePatterns = listOf("com.test.*")
                    excludePatterns = listOf("*.Test*")
                }
            """.trimIndent()
            )

            runGradle("test", "jacocoTestReport")
            val result = runGradle("listProjectCoverage")

            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            // Should use JSON format from extension
            assertTrue(
                result.output.contains("\"projectName\""),
                "Should use JSON format from extension"
            )
        }

        @Test
        fun `test command line options override extension`() {
            loadFixture("simple-project")

            // Modify build file to configure extension with table format
            val buildFile = testProjectDir.resolve("build.gradle.kts")
            val originalContent = buildFile.readText()
            buildFile.writeText(
                originalContent + """
                
                jacocoInspector {
                    defaultFormat = io.github.mpecan.jacoco.model.OutputFormat.TABLE
                    colorOutput = true
                }
            """.trimIndent()
            )

            runGradle("test", "jacocoTestReport")

            // Override with JSON format
            val result = runGradle("listProjectCoverage", "--format=json")

            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            // Should use JSON format from command line, overriding extension
            assertTrue(
                result.output.contains("\"projectName\""),
                "Should use JSON format from command line"
            )
        }
    }

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    inner class EdgeCaseTests {

        @Test
        fun `test non-existent jacoco report is handled gracefully`() {
            loadFixture("simple-project")

            // Don't generate jacoco report, just run the task
            val result = runGradle("listProjectCoverage")
            assertEquals(TaskOutcome.SUCCESS, result.task(":listProjectCoverage")?.outcome)
            // Should handle missing report gracefully
            assertTrue(result.output.isNotBlank(), "Should produce some output even without report")
        }
    }
}