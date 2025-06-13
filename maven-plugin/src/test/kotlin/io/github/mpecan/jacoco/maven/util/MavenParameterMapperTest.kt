package io.github.mpecan.jacoco.maven.util

import io.github.mpecan.jacoco.formatter.*
import io.github.mpecan.jacoco.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MavenParameterMapperTest {

    @Test
    fun `should create empty filter when no parameters provided`() {
        val filter = MavenParameterMapper.buildFilter(
            minCoverage = null,
            coverageType = null,
            minClassCoverage = null, maxClassCoverage = null,
            minMethodCoverage = null, maxMethodCoverage = null,
            minLineCoverage = null, maxLineCoverage = null,
            minBranchCoverage = null, maxBranchCoverage = null,
            minInstructionCoverage = null, maxInstructionCoverage = null,
            minComplexityCoverage = null, maxComplexityCoverage = null,
            includePatterns = null, excludePatterns = null,
            packageFilter = null
        )

        assertThat(filter.minThresholds).isEmpty()
        assertThat(filter.maxThresholds).isEmpty()
        assertThat(filter.includePatterns).isEmpty()
        assertThat(filter.excludePatterns).isEmpty()
    }

    @Test
    fun `should apply generic minCoverage to LINE type by default`() {
        val filter = MavenParameterMapper.buildFilter(
            minCoverage = 80.0,
            coverageType = null,
            minClassCoverage = null, maxClassCoverage = null,
            minMethodCoverage = null, maxMethodCoverage = null,
            minLineCoverage = null, maxLineCoverage = null,
            minBranchCoverage = null, maxBranchCoverage = null,
            minInstructionCoverage = null, maxInstructionCoverage = null,
            minComplexityCoverage = null, maxComplexityCoverage = null,
            includePatterns = null, excludePatterns = null,
            packageFilter = null
        )

        assertThat(filter.minThresholds).containsEntry(CoverageType.LINE, 0.8)
    }

    @Test
    fun `should create TableFormatter for TABLE format`() {
        val formatter = MavenParameterMapper.createFormatter(OutputFormat.TABLE, true)
        assertThat(formatter).isInstanceOf(TableFormatter::class.java)
    }

    @Test
    fun `should create JsonFormatter for JSON format`() {
        val formatter = MavenParameterMapper.createFormatter(OutputFormat.JSON, false)
        assertThat(formatter).isInstanceOf(JsonFormatter::class.java)
    }

    @Test
    fun `should apply generic minCoverage to specified coverage type`() {
        val filter = MavenParameterMapper.buildFilter(
            minCoverage = 90.0,
            coverageType = "BRANCH",
            minClassCoverage = null, maxClassCoverage = null,
            minMethodCoverage = null, maxMethodCoverage = null,
            minLineCoverage = null, maxLineCoverage = null,
            minBranchCoverage = null, maxBranchCoverage = null,
            minInstructionCoverage = null, maxInstructionCoverage = null,
            minComplexityCoverage = null, maxComplexityCoverage = null,
            includePatterns = null, excludePatterns = null,
            packageFilter = null
        )

        assertThat(filter.minThresholds).containsEntry(CoverageType.BRANCH, 0.9)
    }

    @Test
    fun `should apply specific coverage thresholds`() {
        val filter = MavenParameterMapper.buildFilter(
            minCoverage = null,
            coverageType = null,
            minClassCoverage = 70.0, maxClassCoverage = 95.0,
            minMethodCoverage = 75.0, maxMethodCoverage = 90.0,
            minLineCoverage = 80.0, maxLineCoverage = 98.0,
            minBranchCoverage = 65.0, maxBranchCoverage = 85.0,
            minInstructionCoverage = 85.0, maxInstructionCoverage = 100.0,
            minComplexityCoverage = 60.0, maxComplexityCoverage = 95.0,
            includePatterns = null, excludePatterns = null,
            packageFilter = null
        )

        assertThat(filter.minThresholds).containsEntry(CoverageType.CLASS, 0.7)
        assertThat(filter.maxThresholds).containsEntry(CoverageType.CLASS, 0.95)
        assertThat(filter.minThresholds).containsEntry(CoverageType.METHOD, 0.75)
        assertThat(filter.maxThresholds).containsEntry(CoverageType.METHOD, 0.9)
        assertThat(filter.minThresholds).containsEntry(CoverageType.LINE, 0.8)
        assertThat(filter.maxThresholds).containsEntry(CoverageType.LINE, 0.98)
        assertThat(filter.minThresholds).containsEntry(CoverageType.BRANCH, 0.65)
        assertThat(filter.maxThresholds).containsEntry(CoverageType.BRANCH, 0.85)
        assertThat(filter.minThresholds).containsEntry(CoverageType.INSTRUCTION, 0.85)
        assertThat(filter.maxThresholds).containsEntry(CoverageType.INSTRUCTION, 1.0)
        assertThat(filter.minThresholds).containsEntry(CoverageType.COMPLEXITY, 0.6)
        assertThat(filter.maxThresholds).containsEntry(CoverageType.COMPLEXITY, 0.95)
    }

    @Test
    fun `specific thresholds should override generic minCoverage`() {
        val filter = MavenParameterMapper.buildFilter(
            minCoverage = 80.0,
            coverageType = "LINE",
            minClassCoverage = null, maxClassCoverage = null,
            minMethodCoverage = null, maxMethodCoverage = null,
            minLineCoverage = 90.0, maxLineCoverage = null,
            minBranchCoverage = null, maxBranchCoverage = null,
            minInstructionCoverage = null, maxInstructionCoverage = null,
            minComplexityCoverage = null, maxComplexityCoverage = null,
            includePatterns = null, excludePatterns = null,
            packageFilter = null
        )

        // Specific threshold should override generic
        assertThat(filter.minThresholds).containsEntry(CoverageType.LINE, 0.9)
    }

    @Test
    fun `should handle include and exclude patterns`() {
        val includePatterns = listOf("com.example.**", "org.test.*")
        val excludePatterns = listOf("**.test.**", "**.Mock*")

        val filter = MavenParameterMapper.buildFilter(
            minCoverage = null,
            coverageType = null,
            minClassCoverage = null, maxClassCoverage = null,
            minMethodCoverage = null, maxMethodCoverage = null,
            minLineCoverage = null, maxLineCoverage = null,
            minBranchCoverage = null, maxBranchCoverage = null,
            minInstructionCoverage = null, maxInstructionCoverage = null,
            minComplexityCoverage = null, maxComplexityCoverage = null,
            includePatterns = includePatterns, excludePatterns = excludePatterns,
            packageFilter = null
        )

        assertThat(filter.includePatterns).containsExactlyElementsOf(includePatterns)
        assertThat(filter.excludePatterns).containsExactlyElementsOf(excludePatterns)
    }

    @Test
    fun `should add package filter to include patterns`() {
        val includePatterns = listOf("com.example.**")
        val packageFilter = "org.test"

        val filter = MavenParameterMapper.buildFilter(
            minCoverage = null,
            coverageType = null,
            minClassCoverage = null, maxClassCoverage = null,
            minMethodCoverage = null, maxMethodCoverage = null,
            minLineCoverage = null, maxLineCoverage = null,
            minBranchCoverage = null, maxBranchCoverage = null,
            minInstructionCoverage = null, maxInstructionCoverage = null,
            minComplexityCoverage = null, maxComplexityCoverage = null,
            includePatterns = includePatterns, excludePatterns = null,
            packageFilter = packageFilter
        )

        assertThat(filter.includePatterns).containsExactly("com.example.**", "org.test*")
    }

    @Test
    fun `should ignore blank package filter`() {
        val filter = MavenParameterMapper.buildFilter(
            minCoverage = null,
            coverageType = null,
            minClassCoverage = null, maxClassCoverage = null,
            minMethodCoverage = null, maxMethodCoverage = null,
            minLineCoverage = null, maxLineCoverage = null,
            minBranchCoverage = null, maxBranchCoverage = null,
            minInstructionCoverage = null, maxInstructionCoverage = null,
            minComplexityCoverage = null, maxComplexityCoverage = null,
            includePatterns = null, excludePatterns = null,
            packageFilter = "   "
        )

        assertThat(filter.includePatterns).isEmpty()
    }

    @Test
    fun `should create CsvFormatter for CSV format`() {
        val formatter = MavenParameterMapper.createFormatter(OutputFormat.CSV, true)
        assertThat(formatter).isInstanceOf(CsvFormatter::class.java)
    }

    @Test
    fun `should create MarkdownFormatter for MARKDOWN format`() {
        val formatter = MavenParameterMapper.createFormatter(OutputFormat.MARKDOWN, false)
        assertThat(formatter).isInstanceOf(MarkdownFormatter::class.java)
    }
}