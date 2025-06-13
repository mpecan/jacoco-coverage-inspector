package io.github.mpecan.jacoco.tasks

import io.github.mpecan.jacoco.JacocoInspectorExtension
import io.github.mpecan.jacoco.model.*
import io.github.mpecan.jacoco.parser.JacocoXmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option

/**
 * Base class for all coverage inspection tasks
 */
@Suppress("unused")
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
            val thresholds = CoverageThresholds(
                minClass = minClassCoverage.orNull,
                maxClass = maxClassCoverage.orNull,
                minMethod = minMethodCoverage.orNull,
                maxMethod = maxMethodCoverage.orNull,
                minLine = minLineCoverage.orNull,
                maxLine = maxLineCoverage.orNull,
                minBranch = minBranchCoverage.orNull,
                maxBranch = maxBranchCoverage.orNull,
                minInstruction = minInstructionCoverage.orNull,
                maxInstruction = maxInstructionCoverage.orNull,
                minComplexity = minComplexityCoverage.orNull,
                maxComplexity = maxComplexityCoverage.orNull
            )
            
            val filterPatterns = FilterPatterns(
                includePatterns = includePatterns.getOrElse(emptyList()),
                excludePatterns = excludePatterns.getOrElse(emptyList()),
                packageFilter = packageFilter.orNull
            )
            
            val filter = CoverageFilterBuilder().buildFilter(
                minCoverage = minCoverage.orNull,
                coverageType = coverageType.orNull,
                thresholds = thresholds,
                filterPatterns = filterPatterns
            )

            // Perform task-specific execution
            generateOutput(coverageData, filter)
        } else {
            listOf<ClassCoverageData>()
        }

        // Format and print the output
        val formatter = FormatterFactory().createFormatter(
            format = format.get(),
            colorOutput = colorOutput.get()
        )
        println(formatter.format(output))
    }

    /**
     * Generate the output data for this specific task type
     */
    open fun generateOutput(coverageData: ProjectCoverageData, filter: CoverageFilter): Any {
        return OutputGenerator().generateFileOutput(coverageData, filter)
    }

    /**
     * Apply defaults from the extension
     */
    fun applyDefaults(extension: JacocoInspectorExtension) {
        applyBasicDefaults(extension)
        applyThresholdDefaults(extension)
        applyPatternDefaults(extension)
    }
    
    private fun applyBasicDefaults(extension: JacocoInspectorExtension) {
        if (!format.isPresent) {
            format.set(extension.defaultFormat)
        }
        if (!colorOutput.isPresent) {
            colorOutput.set(extension.colorOutput)
        }
    }
    
    private fun applyThresholdDefaults(extension: JacocoInspectorExtension) {
        applyMinThresholdDefaults(extension)
        applyMaxThresholdDefaults(extension)
    }
    
    private fun applyMinThresholdDefaults(extension: JacocoInspectorExtension) {
        applyPropertyDefault(minClassCoverage, extension.minClassCoverage)
        applyPropertyDefault(minMethodCoverage, extension.minMethodCoverage)
        applyPropertyDefault(minLineCoverage, extension.minLineCoverage)
        applyPropertyDefault(minBranchCoverage, extension.minBranchCoverage)
        applyPropertyDefault(minInstructionCoverage, extension.minInstructionCoverage)
        applyPropertyDefault(minComplexityCoverage, extension.minComplexityCoverage)
    }
    
    private fun applyMaxThresholdDefaults(extension: JacocoInspectorExtension) {
        applyPropertyDefault(maxClassCoverage, extension.maxClassCoverage)
        applyPropertyDefault(maxMethodCoverage, extension.maxMethodCoverage)
        applyPropertyDefault(maxLineCoverage, extension.maxLineCoverage)
        applyPropertyDefault(maxBranchCoverage, extension.maxBranchCoverage)
        applyPropertyDefault(maxInstructionCoverage, extension.maxInstructionCoverage)
        applyPropertyDefault(maxComplexityCoverage, extension.maxComplexityCoverage)
    }
    
    private fun applyPropertyDefault(property: Property<Double>, extensionValue: Double?) {
        if (!property.isPresent && extensionValue != null) {
            property.set(extensionValue)
        }
    }
    
    private fun applyPatternDefaults(extension: JacocoInspectorExtension) {
        if ((!includePatterns.isPresent || includePatterns.get().isEmpty()) && 
            extension.includePatterns.isNotEmpty()) {
            includePatterns.set(extension.includePatterns)
        }
        if ((!excludePatterns.isPresent || excludePatterns.get().isEmpty()) && 
            extension.excludePatterns.isNotEmpty()) {
            excludePatterns.set(extension.excludePatterns)
        }
    }

}