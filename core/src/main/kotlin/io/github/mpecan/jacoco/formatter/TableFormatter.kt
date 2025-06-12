package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.model.*

/**
 * Formats coverage data as human-readable tables
 */
class TableFormatter(
    private val colorOutput: Boolean = true,
    private val outputFormat: OutputFormat = OutputFormat.TABLE
) : CoverageFormatter {
    
    companion object {
        // ANSI color codes
        private const val RESET = "\u001B[0m"
        private const val RED = "\u001B[31m"
        private const val YELLOW = "\u001B[33m"
        private const val GREEN = "\u001B[32m"
        private const val BOLD = "\u001B[1m"
        
        // Coverage thresholds for color coding
        private const val HIGH_COVERAGE = 80.0
        private const val MEDIUM_COVERAGE = 60.0
    }
    
    override fun format(data: Any): String {
        return when (data) {
            is ProjectCoverageData -> formatProjectCoverage(data)
            is List<*> -> formatList(data)
            else -> data.toString()
        }
    }
    
    private fun formatProjectCoverage(project: ProjectCoverageData): String {
        val sb = StringBuilder()
        
        sb.appendLine()
        sb.appendLine(bold("PROJECT COVERAGE SUMMARY"))
        sb.appendLine("=" * 60)
        sb.appendLine()
        sb.appendLine("Project: ${project.projectName}")
        sb.appendLine()
        
        // Format coverage counters
        sb.append(formatCounters(project.counters))
        
        return sb.toString()
    }
    
    private fun formatList(list: List<*>): String {
        if (list.isEmpty()) {
            return "\nNo items match the specified filters.\n"
        }
        
        return when (val first = list.first()) {
            is PackageCoverageData -> formatPackageList(list.filterIsInstance<PackageCoverageData>())
            is ClassCoverageData -> formatClassList(list.filterIsInstance<ClassCoverageData>())
            else -> list.joinToString("\n")
        }
    }
    
    private fun formatPackageList(packages: List<PackageCoverageData>): String {
        val sb = StringBuilder()
        
        sb.appendLine()
        sb.appendLine(bold("PACKAGE COVERAGE"))
        sb.appendLine("=" * 80)
        sb.appendLine()
        
        // Header
        sb.appendLine(String.format(java.util.Locale.US, "%-40s %10s %10s %10s %10s", 
            "Package", "Class", "Method", "Line", "Branch"))
        sb.appendLine("-" * 80)
        
        // Data rows
        for (pkg in packages) {
            val className = formatPercentage(pkg.getCounter(CoverageType.CLASS))
            val methodName = formatPercentage(pkg.getCounter(CoverageType.METHOD))
            val lineName = formatPercentage(pkg.getCounter(CoverageType.LINE))
            val branchName = formatPercentage(pkg.getCounter(CoverageType.BRANCH))
            
            sb.appendLine(String.format(java.util.Locale.US, "%-40s %10s %10s %10s %10s",
                truncate(pkg.packageName, 40),
                className,
                methodName,
                lineName,
                branchName
            ))
        }
        
        sb.appendLine()
        return sb.toString()
    }
    
    private fun formatClassList(classes: List<ClassCoverageData>): String {
        val sb = StringBuilder()
        
        sb.appendLine()
        sb.appendLine(bold("FILE COVERAGE"))
        sb.appendLine("=" * 100)
        sb.appendLine()
        
        // Header
        sb.appendLine(String.format(java.util.Locale.US, "%-50s %-20s %10s %10s %10s %10s", 
            "Class", "Source", "Class", "Method", "Line", "Branch"))
        sb.appendLine("-" * 100)
        
        // Data rows
        for (cls in classes) {
            val className = formatPercentage(cls.getCounter(CoverageType.CLASS))
            val methodName = formatPercentage(cls.getCounter(CoverageType.METHOD))
            val lineName = formatPercentage(cls.getCounter(CoverageType.LINE))
            val branchName = formatPercentage(cls.getCounter(CoverageType.BRANCH))
            
            sb.appendLine(String.format(java.util.Locale.US, "%-50s %-20s %10s %10s %10s %10s",
                truncate(cls.className, 50),
                truncate(cls.sourceFileName ?: "N/A", 20),
                className,
                methodName,
                lineName,
                branchName
            ))
        }
        
        sb.appendLine()
        return sb.toString()
    }
    
    private fun formatCounters(counters: Map<CoverageType, CoverageCounter>): String {
        val sb = StringBuilder()
        
        sb.appendLine(String.format(java.util.Locale.US, "%-15s %10s %10s %10s %15s", 
            "Type", "Covered", "Missed", "Total", "Coverage"))
        sb.appendLine("-" * 60)
        
        for (type in CoverageType.values()) {
            val counter = counters[type]
            if (counter != null) {
                val coverageStr = formatPercentage(counter)
                sb.appendLine(String.format(java.util.Locale.US, "%-15s %10d %10d %10d %15s",
                    type.toString(),
                    counter.covered,
                    counter.missed,
                    counter.total,
                    coverageStr
                ))
            }
        }
        
        sb.appendLine()
        return sb.toString()
    }
    
    private fun formatPercentage(counter: CoverageCounter?): String {
        if (counter == null) return "N/A"
        
        val percentage = counter.percentage
        val formatted = "${CoverageFormatter.formatPercentage(percentage, 1)}%"
        
        return if (colorOutput) {
            when {
                percentage >= HIGH_COVERAGE -> green(formatted)
                percentage >= MEDIUM_COVERAGE -> yellow(formatted)
                else -> red(formatted)
            }
        } else {
            formatted
        }
    }
    
    private fun truncate(str: String, maxLength: Int): String {
        return if (str.length <= maxLength) {
            str
        } else {
            str.substring(0, maxLength - 3) + "..."
        }
    }
    
    private fun bold(text: String): String {
        return if (colorOutput) "$BOLD$text$RESET" else text
    }
    
    private fun red(text: String): String {
        return if (colorOutput) "$RED$text$RESET" else text
    }
    
    private fun yellow(text: String): String {
        return if (colorOutput) "$YELLOW$text$RESET" else text
    }
    
    private fun green(text: String): String {
        return if (colorOutput) "$GREEN$text$RESET" else text
    }
    
    private operator fun String.times(n: Int): String {
        return this.repeat(n)
    }
}