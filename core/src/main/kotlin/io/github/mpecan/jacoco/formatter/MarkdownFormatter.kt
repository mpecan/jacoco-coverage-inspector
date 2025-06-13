package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.model.*

/**
 * Formats coverage data as Markdown output
 */
class MarkdownFormatter : CoverageFormatter {
    
    companion object {
        private const val HIGH_COVERAGE = 80.0
        private const val MEDIUM_COVERAGE = 60.0
    }
    
    override fun format(data: Any): String {
        return when (data) {
            is ProjectCoverageData -> formatProjectCoverage(data)
            is List<*> -> formatList(data)
            else -> escapeMarkdown(data.toString())
        }
    }
    
    private fun formatProjectCoverage(project: ProjectCoverageData): String {
        val sb = StringBuilder()
        
        sb.appendLine("# Project Coverage Summary")
        sb.appendLine()
        sb.appendLine("**Project:** ${escapeMarkdown(project.projectName)}")
        sb.appendLine()
        
        // Coverage table
        sb.appendLine("## Coverage Overview")
        sb.appendLine()
        sb.appendLine(formatCountersTable(project.counters))
        
        // Package details if available
        if (project.packages.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("## Package Details")
            sb.appendLine()
            sb.append(formatPackageList(project.packages))
        }
        
        return sb.toString()
    }
    
    private fun formatList(list: List<*>): String {
        if (list.isEmpty()) {
            return "*No items match the specified filters.*\n"
        }
        
        return when (list.first()) {
            is PackageCoverageData -> formatPackageList(list.filterIsInstance<PackageCoverageData>())
            is ClassCoverageData -> formatClassList(list.filterIsInstance<ClassCoverageData>())
            else -> list.joinToString("\n") { "- ${escapeMarkdown(it.toString())}" }
        }
    }
    
    private fun formatPackageList(packages: List<PackageCoverageData>): String {
        val sb = StringBuilder()
        
        sb.appendLine("## Package Coverage")
        sb.appendLine()
        
        // Table header
        sb.appendLine("| Package | Class | Method | Line | Branch |")
        sb.appendLine("|---------|-------|--------|------|--------|")
        
        // Data rows
        for (pkg in packages) {
            val classPercentage = formatPercentageCell(pkg.getCounter(CoverageType.CLASS))
            val methodPercentage = formatPercentageCell(pkg.getCounter(CoverageType.METHOD))
            val linePercentage = formatPercentageCell(pkg.getCounter(CoverageType.LINE))
            val branchPercentage = formatPercentageCell(pkg.getCounter(CoverageType.BRANCH))
            
            sb.appendLine("| ${escapeMarkdown(pkg.packageName)} | $classPercentage | $methodPercentage | $linePercentage | $branchPercentage |")
        }
        
        sb.appendLine()
        return sb.toString()
    }
    
    private fun formatClassList(classes: List<ClassCoverageData>): String {
        val sb = StringBuilder()
        
        sb.appendLine("## File Coverage")
        sb.appendLine()
        
        // Table header
        sb.appendLine("| Class | Source File | Class | Method | Line | Branch |")
        sb.appendLine("|-------|-------------|-------|--------|------|--------|")
        
        // Data rows
        for (cls in classes) {
            val sourceFile = cls.sourceFileName ?: "N/A"
            val classPercentage = formatPercentageCell(cls.getCounter(CoverageType.CLASS))
            val methodPercentage = formatPercentageCell(cls.getCounter(CoverageType.METHOD))
            val linePercentage = formatPercentageCell(cls.getCounter(CoverageType.LINE))
            val branchPercentage = formatPercentageCell(cls.getCounter(CoverageType.BRANCH))
            
            sb.appendLine("| ${escapeMarkdown(cls.className)} | ${escapeMarkdown(sourceFile)} | $classPercentage | $methodPercentage | $linePercentage | $branchPercentage |")
        }
        
        sb.appendLine()
        return sb.toString()
    }
    
    private fun formatCountersTable(counters: Map<CoverageType, CoverageCounter>): String {
        val sb = StringBuilder()
        
        // Table header
        sb.appendLine("| Type | Covered | Missed | Total | Coverage |")
        sb.appendLine("|------|---------|--------|-------|----------|")
        
        // Data rows
        for (type in CoverageType.values()) {
            val counter = counters[type]
            if (counter != null) {
                val coverageCell = formatPercentageCell(counter)
                sb.appendLine("| ${type.name.lowercase().replaceFirstChar { it.uppercase() }} | ${counter.covered} | ${counter.missed} | ${counter.total} | $coverageCell |")
            }
        }
        
        return sb.toString()
    }
    
    private fun formatPercentageCell(counter: CoverageCounter?): String {
        if (counter == null) return "N/A"
        
        val percentage = counter.percentage
        val formatted = "${CoverageFormatter.formatPercentage(percentage, 1)}%"
        
        // Add emoji indicators based on coverage level
        return when {
            percentage >= HIGH_COVERAGE -> "🟢 $formatted"
            percentage >= MEDIUM_COVERAGE -> "🟡 $formatted"
            else -> "🔴 $formatted"
        }
    }
    
    private fun escapeMarkdown(str: String): String {
        // Escape special Markdown characters
        val specialChars = arrayOf("\\", "`", "*", "_", "{", "}", "[", "]", "(", ")", "#", "+", "-", ".", "!", "|")
        var result = str
        
        for (char in specialChars) {
            result = result.replace(char, "\\$char")
        }
        
        return result
    }
}