package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.JacocoInspectorExtension
import io.github.mpecan.jacoco.formatter.*
import io.github.mpecan.jacoco.model.*
import io.github.mpecan.jacoco.parser.JacocoXmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * Base class for all coverage inspection tasks
 */
abstract class BaseInspectorTask : DefaultTask() {

    /**
     * The JaCoCo XML report file to analyze
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val jacocoReportFile: RegularFileProperty

    /**
     * Output format for the report
     */
    @get:Input
    @get:Optional
    abstract val format: Property<OutputFormat>

    /**
     * Whether to use color output in terminal
     */
    @get:Input
    @get:Optional
    @get:Option(option = "color", description = "Enable color output in terminal")
    abstract val colorOutput: Property<Boolean>

    /**
     * Minimum coverage thresholds for filtering
     */
    @get:Input
    @get:Optional
    @get:Option(option = "minClassCoverage", description = "Minimum class coverage percentage")
    abstract val minClassCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(option = "minMethodCoverage", description = "Minimum method coverage percentage")
    abstract val minMethodCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(option = "minLineCoverage", description = "Minimum line coverage percentage")
    abstract val minLineCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(option = "minBranchCoverage", description = "Minimum branch coverage percentage")
    abstract val minBranchCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(
        option = "minInstructionCoverage",
        description = "Minimum instruction coverage percentage"
    )
    abstract val minInstructionCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(
        option = "minComplexityCoverage",
        description = "Minimum complexity coverage percentage"
    )
    abstract val minComplexityCoverage: Property<Double>

    /**
     * Maximum coverage thresholds for filtering
     */
    @get:Input
    @get:Optional
    @get:Option(option = "maxClassCoverage", description = "Maximum class coverage percentage")
    abstract val maxClassCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(option = "maxMethodCoverage", description = "Maximum method coverage percentage")
    abstract val maxMethodCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(option = "maxLineCoverage", description = "Maximum line coverage percentage")
    abstract val maxLineCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(option = "maxBranchCoverage", description = "Maximum branch coverage percentage")
    abstract val maxBranchCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(
        option = "maxInstructionCoverage",
        description = "Maximum instruction coverage percentage"
    )
    abstract val maxInstructionCoverage: Property<Double>

    @get:Input
    @get:Optional
    @get:Option(
        option = "maxComplexityCoverage",
        description = "Maximum complexity coverage percentage"
    )
    abstract val maxComplexityCoverage: Property<Double>

    /**
     * Include patterns for filtering
     */
    @get:Input
    @get:Optional
    @get:Option(
        option = "includePatterns",
        description = "Include patterns for filtering classes/packages"
    )
    abstract val includePatterns: ListProperty<String>

    /**
     * Exclude patterns for filtering
     */
    @get:Input
    @get:Optional
    @get:Option(
        option = "excludePatterns",
        description = "Exclude patterns for filtering classes/packages"
    )
    abstract val excludePatterns: ListProperty<String>

    /**
     * Generic minimum coverage threshold - applies to the primary coverage type
     */
    @get:Input
    @get:Optional
    @get:Option(
        option = "minCoverage",
        description = "Minimum coverage percentage (applies to primary coverage type)"
    )
    abstract val minCoverage: Property<Double>

    /**
     * Coverage type for filtering
     */
    @get:Input
    @get:Optional
    abstract val coverageType: Property<CoverageType>

    /**
     * Package filter pattern
     */
    @get:Input
    @get:Optional
    @get:Option(option = "packageFilter", description = "Package name filter pattern")
    abstract val packageFilter: Property<String>

    init {
        group = "verification"
    }

    /**
     * Setter for format option that accepts string values
     */
    @Option(option = "format", description = "Output format (table, json, csv, markdown)")
    fun setFormatOption(formatString: String) {
        format.set(OutputFormat.valueOf(formatString.uppercase()))
    }

    /**
     * Setter for coverageType option that accepts string values
     */
    @Option(
        option = "coverageType",
        description = "Coverage type for filtering (INSTRUCTION, BRANCH, LINE, COMPLEXITY, METHOD, CLASS)"
    )
    fun setCoverageTypeOption(typeString: String) {
        coverageType.set(CoverageType.valueOf(typeString.uppercase()))
    }

    @TaskAction
    fun execute() {
        val reportFile = jacocoReportFile.get().asFile
        val output = if (reportFile.exists()) {

            // Parse the report
            val parser = JacocoXmlParser()
            val coverageData = parser.parseReport(reportFile)

            // Build the filter
            val filter = buildCoverageFilter()

            // Perform task-specific execution
            generateOutput(coverageData, filter)

        } else {
            listOf<ClassCoverageData>()
        }

        // Format and print the output
        val formatter = createFormatter()
        println(formatter.format(output))
    }

    /**
     * Generate the output data for this specific task type
     */
    protected abstract fun generateOutput(
        coverageData: ProjectCoverageData,
        filter: CoverageFilter
    ): Any

    /**
     * Apply defaults from the extension
     */
    fun applyDefaults(extension: JacocoInspectorExtension) {
        if (!format.isPresent) {
            format.set(extension.defaultFormat)
        }
        if (!colorOutput.isPresent) {
            colorOutput.set(extension.colorOutput)
        }

        // Apply threshold defaults
        if (!minClassCoverage.isPresent && extension.minClassCoverage != null) {
            minClassCoverage.set(extension.minClassCoverage)
        }
        if (!minMethodCoverage.isPresent && extension.minMethodCoverage != null) {
            minMethodCoverage.set(extension.minMethodCoverage)
        }
        if (!minLineCoverage.isPresent && extension.minLineCoverage != null) {
            minLineCoverage.set(extension.minLineCoverage)
        }
        if (!minBranchCoverage.isPresent && extension.minBranchCoverage != null) {
            minBranchCoverage.set(extension.minBranchCoverage)
        }
        if (!minInstructionCoverage.isPresent && extension.minInstructionCoverage != null) {
            minInstructionCoverage.set(extension.minInstructionCoverage)
        }
        if (!minComplexityCoverage.isPresent && extension.minComplexityCoverage != null) {
            minComplexityCoverage.set(extension.minComplexityCoverage)
        }

        // Apply max threshold defaults
        if (!maxClassCoverage.isPresent && extension.maxClassCoverage != null) {
            maxClassCoverage.set(extension.maxClassCoverage)
        }
        if (!maxMethodCoverage.isPresent && extension.maxMethodCoverage != null) {
            maxMethodCoverage.set(extension.maxMethodCoverage)
        }
        if (!maxLineCoverage.isPresent && extension.maxLineCoverage != null) {
            maxLineCoverage.set(extension.maxLineCoverage)
        }
        if (!maxBranchCoverage.isPresent && extension.maxBranchCoverage != null) {
            maxBranchCoverage.set(extension.maxBranchCoverage)
        }
        if (!maxInstructionCoverage.isPresent && extension.maxInstructionCoverage != null) {
            maxInstructionCoverage.set(extension.maxInstructionCoverage)
        }
        if (!maxComplexityCoverage.isPresent && extension.maxComplexityCoverage != null) {
            maxComplexityCoverage.set(extension.maxComplexityCoverage)
        }

        // Apply pattern defaults
        if (!includePatterns.isPresent && extension.includePatterns.isNotEmpty()) {
            includePatterns.set(extension.includePatterns)
        }
        if (!excludePatterns.isPresent && extension.excludePatterns.isNotEmpty()) {
            excludePatterns.set(extension.excludePatterns)
        }
    }

    private fun buildCoverageFilter(): CoverageFilter {
        val minThresholds = mutableMapOf<CoverageType, Double>()
        val maxThresholds = mutableMapOf<CoverageType, Double>()

        // Handle generic minCoverage option - apply to specified coverage type or default to LINE
        minCoverage.orNull?.let { coverage ->
            val targetType = coverageType.getOrElse(CoverageType.LINE)
            minThresholds[targetType] = coverage / 100.0
        }

        // Build min thresholds from specific options (these override the generic minCoverage)
        minClassCoverage.orNull?.let { minThresholds[CoverageType.CLASS] = it / 100.0 }
        minMethodCoverage.orNull?.let { minThresholds[CoverageType.METHOD] = it / 100.0 }
        minLineCoverage.orNull?.let { minThresholds[CoverageType.LINE] = it / 100.0 }
        minBranchCoverage.orNull?.let { minThresholds[CoverageType.BRANCH] = it / 100.0 }
        minInstructionCoverage.orNull?.let { minThresholds[CoverageType.INSTRUCTION] = it / 100.0 }
        minComplexityCoverage.orNull?.let { minThresholds[CoverageType.COMPLEXITY] = it / 100.0 }

        // Build max thresholds
        maxClassCoverage.orNull?.let { maxThresholds[CoverageType.CLASS] = it / 100.0 }
        maxMethodCoverage.orNull?.let { maxThresholds[CoverageType.METHOD] = it / 100.0 }
        maxLineCoverage.orNull?.let { maxThresholds[CoverageType.LINE] = it / 100.0 }
        maxBranchCoverage.orNull?.let { maxThresholds[CoverageType.BRANCH] = it / 100.0 }
        maxInstructionCoverage.orNull?.let { maxThresholds[CoverageType.INSTRUCTION] = it / 100.0 }
        maxComplexityCoverage.orNull?.let { maxThresholds[CoverageType.COMPLEXITY] = it / 100.0 }

        // Handle package filter - add it to include patterns
        val includePatternsList = includePatterns.getOrElse(emptyList()).toMutableList()
        packageFilter.orNull?.let { pattern ->
            includePatternsList.add("$pattern*")
        }

        return CoverageFilter(
            minThresholds = minThresholds,
            maxThresholds = maxThresholds,
            includePatterns = includePatternsList,
            excludePatterns = excludePatterns.getOrElse(emptyList())
        )
    }

    private fun createFormatter(): CoverageFormatter {
        logger.debug("Formatting output with ${format.get()} format")
        return when (format.get()) {
            OutputFormat.TABLE -> TableFormatter(
                colorOutput = colorOutput.get(),
                outputFormat = format.get()
            )
            OutputFormat.JSON -> JsonFormatter()
            OutputFormat.CSV -> CsvFormatter()
            OutputFormat.MARKDOWN -> MarkdownFormatter()
        }
    }
}