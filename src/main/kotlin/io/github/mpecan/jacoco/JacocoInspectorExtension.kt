package io.github.mpecan.jacoco

import io.github.mpecan.jacoco.model.OutputFormat

/**
 * Extension for configuring the JaCoCo Coverage Inspector plugin
 */
open class JacocoInspectorExtension {
    /**
     * Default output format for all tasks
     */
    var defaultFormat: OutputFormat = OutputFormat.TABLE
    
    /**
     * Whether to use color output in terminal
     */
    var colorOutput: Boolean = true
    
    /**
     * Default minimum coverage thresholds
     */
    var minClassCoverage: Double? = null
    var minMethodCoverage: Double? = null
    var minLineCoverage: Double? = null
    var minBranchCoverage: Double? = null
    var minInstructionCoverage: Double? = null
    var minComplexityCoverage: Double? = null
    
    /**
     * Default maximum coverage thresholds
     */
    var maxClassCoverage: Double? = null
    var maxMethodCoverage: Double? = null
    var maxLineCoverage: Double? = null
    var maxBranchCoverage: Double? = null
    var maxInstructionCoverage: Double? = null
    var maxComplexityCoverage: Double? = null
    
    /**
     * Default exclude patterns
     */
    var excludePatterns: List<String> = emptyList()
    
    /**
     * Default include patterns
     */
    var includePatterns: List<String> = emptyList()
}