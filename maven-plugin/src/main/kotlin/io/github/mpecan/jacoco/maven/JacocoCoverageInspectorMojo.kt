package io.github.mpecan.jacoco.maven

import io.github.mpecan.jacoco.maven.util.MavenParameterMapper
import io.github.mpecan.jacoco.model.*
import io.github.mpecan.jacoco.parser.JacocoXmlParser
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

/**
 * Base class for all JaCoCo coverage inspector mojos
 */
abstract class JacocoCoverageInspectorMojo : AbstractMojo() {

    /**
     * The JaCoCo XML report file to analyze
     */
    @Parameter(property = "jacoco.reportFile", 
               defaultValue = "\${project.reporting.outputDirectory}/jacoco/jacoco.xml")
    internal lateinit var jacocoReportFile: File

    /**
     * Output format for the report
     */
    @Parameter(property = "jacoco.format", defaultValue = "TABLE")
    internal lateinit var format: String

    /**
     * Whether to use color output in terminal
     */
    @Parameter(property = "jacoco.colorOutput", defaultValue = "true")
    internal var colorOutput: Boolean = true

    /**
     * Minimum coverage thresholds for filtering
     */
    @Parameter(property = "jacoco.minClassCoverage")
    internal var minClassCoverage: Double? = null

    @Parameter(property = "jacoco.minMethodCoverage")
    internal var minMethodCoverage: Double? = null

    @Parameter(property = "jacoco.minLineCoverage")
    internal var minLineCoverage: Double? = null

    @Parameter(property = "jacoco.minBranchCoverage")
    internal var minBranchCoverage: Double? = null

    @Parameter(property = "jacoco.minInstructionCoverage")
    internal var minInstructionCoverage: Double? = null

    @Parameter(property = "jacoco.minComplexityCoverage")
    internal var minComplexityCoverage: Double? = null

    /**
     * Maximum coverage thresholds for filtering
     */
    @Parameter(property = "jacoco.maxClassCoverage")
    internal var maxClassCoverage: Double? = null

    @Parameter(property = "jacoco.maxMethodCoverage")
    internal var maxMethodCoverage: Double? = null

    @Parameter(property = "jacoco.maxLineCoverage")
    internal var maxLineCoverage: Double? = null

    @Parameter(property = "jacoco.maxBranchCoverage")
    internal var maxBranchCoverage: Double? = null

    @Parameter(property = "jacoco.maxInstructionCoverage")
    internal var maxInstructionCoverage: Double? = null

    @Parameter(property = "jacoco.maxComplexityCoverage")
    internal var maxComplexityCoverage: Double? = null

    /**
     * Include patterns for filtering
     */
    @Parameter(property = "jacoco.includePatterns")
    internal var includePatterns: List<String>? = null

    /**
     * Exclude patterns for filtering
     */
    @Parameter(property = "jacoco.excludePatterns")
    internal var excludePatterns: List<String>? = null

    /**
     * Generic minimum coverage threshold - applies to the primary coverage type
     */
    @Parameter(property = "jacoco.minCoverage")
    internal var minCoverage: Double? = null

    /**
     * Coverage type for filtering
     */
    @Parameter(property = "jacoco.coverageType")
    internal var coverageType: String? = null

    /**
     * Package filter pattern
     */
    @Parameter(property = "jacoco.packageFilter")
    internal var packageFilter: String? = null

    /**
     * The Maven project
     */
    @Parameter(defaultValue = "\${project}", readonly = true, required = true)
    internal lateinit var project: MavenProject

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        if (!jacocoReportFile.exists()) {
            log.warn("JaCoCo report file not found: ${jacocoReportFile.absolutePath}")
            log.info("No coverage data to display")
            return
        }

        try {
            // Parse the report
            val parser = JacocoXmlParser()
            val coverageData = parser.parseReport(jacocoReportFile)

            // Build the filter
            val filter = MavenParameterMapper.buildFilter(
                minCoverage = minCoverage,
                coverageType = coverageType,
                minClassCoverage = minClassCoverage,
                maxClassCoverage = maxClassCoverage,
                minMethodCoverage = minMethodCoverage,
                maxMethodCoverage = maxMethodCoverage,
                minLineCoverage = minLineCoverage,
                maxLineCoverage = maxLineCoverage,
                minBranchCoverage = minBranchCoverage,
                maxBranchCoverage = maxBranchCoverage,
                minInstructionCoverage = minInstructionCoverage,
                maxInstructionCoverage = maxInstructionCoverage,
                minComplexityCoverage = minComplexityCoverage,
                maxComplexityCoverage = maxComplexityCoverage,
                includePatterns = includePatterns,
                excludePatterns = excludePatterns,
                packageFilter = packageFilter
            )

            // Generate the output data
            val output = generateOutput(coverageData, filter)

            // Format and display the output
            val outputFormat = OutputFormat.valueOf(format.uppercase())
            val formatter = MavenParameterMapper.createFormatter(outputFormat, colorOutput)
            
            val formattedOutput = formatter.format(output)
            log.info("\n$formattedOutput")

        } catch (e: Exception) {
            throw MojoExecutionException("Error processing JaCoCo report: ${e.message}", e)
        }
    }

    /**
     * Generate the output data for this specific mojo type
     */
    internal abstract fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any
}