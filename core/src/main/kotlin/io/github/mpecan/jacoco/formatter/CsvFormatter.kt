package io.github.mpecan.jacoco.formatter

import io.github.mpecan.jacoco.model.*

/**
 * Formats coverage data as CSV output
 */
class CsvFormatter : CoverageFormatter {
    
    override fun format(data: Any): String {
        return when (data) {
            is ProjectCoverageData -> formatProjectCoverage(data)
            is List<*> -> formatList(data)
            else -> escapeCsv(data.toString())
        }
    }
    
    private fun formatProjectCoverage(project: ProjectCoverageData): String {
        val sb = StringBuilder()
        
        // Project header
        sb.appendLine("Type,Name,ClassCovered,ClassMissed,ClassTotal,ClassPercentage,MethodCovered,MethodMissed,MethodTotal,MethodPercentage,LineCovered,LineMissed,LineTotal,LinePercentage,BranchCovered,BranchMissed,BranchTotal,BranchPercentage,InstructionCovered,InstructionMissed,InstructionTotal,InstructionPercentage,ComplexityCovered,ComplexityMissed,ComplexityTotal,ComplexityPercentage")
        
        // Project data
        sb.appendLine(formatProjectRow(project))
        
        // Package data if available
        if (project.packages.isNotEmpty()) {
            for (pkg in project.packages) {
                sb.appendLine(formatPackageRow(pkg))
                
                // Class data if available
                for (cls in pkg.classes) {
                    sb.appendLine(formatClassRow(cls))
                }
            }
        }
        
        return sb.toString()
    }
    
    private fun formatList(list: List<*>): String {
        if (list.isEmpty()) {
            return ""
        }
        
        return when (val first = list.first()) {
            is PackageCoverageData -> formatPackageList(list.filterIsInstance<PackageCoverageData>())
            is ClassCoverageData -> formatClassList(list.filterIsInstance<ClassCoverageData>())
            else -> list.joinToString("\n") { escapeCsv(it.toString()) }
        }
    }
    
    private fun formatPackageList(packages: List<PackageCoverageData>): String {
        val sb = StringBuilder()
        
        // Header
        sb.appendLine("Type,Name,ClassCovered,ClassMissed,ClassTotal,ClassPercentage,MethodCovered,MethodMissed,MethodTotal,MethodPercentage,LineCovered,LineMissed,LineTotal,LinePercentage,BranchCovered,BranchMissed,BranchTotal,BranchPercentage,InstructionCovered,InstructionMissed,InstructionTotal,InstructionPercentage,ComplexityCovered,ComplexityMissed,ComplexityTotal,ComplexityPercentage")
        
        // Data rows
        for (pkg in packages) {
            sb.appendLine(formatPackageRow(pkg))
        }
        
        return sb.toString()
    }
    
    private fun formatClassList(classes: List<ClassCoverageData>): String {
        val sb = StringBuilder()
        
        // Header
        sb.appendLine("Type,Name,SourceFile,ClassCovered,ClassMissed,ClassTotal,ClassPercentage,MethodCovered,MethodMissed,MethodTotal,MethodPercentage,LineCovered,LineMissed,LineTotal,LinePercentage,BranchCovered,BranchMissed,BranchTotal,BranchPercentage,InstructionCovered,InstructionMissed,InstructionTotal,InstructionPercentage,ComplexityCovered,ComplexityMissed,ComplexityTotal,ComplexityPercentage")
        
        // Data rows
        for (cls in classes) {
            sb.appendLine(formatClassRow(cls))
        }
        
        return sb.toString()
    }
    
    private fun formatProjectRow(project: ProjectCoverageData): String {
        val counters = project.counters
        return "PROJECT,${escapeCsv(project.projectName)},${formatAllCounters(counters)}"
    }
    
    private fun formatPackageRow(pkg: PackageCoverageData): String {
        val counters = pkg.counters
        return "PACKAGE,${escapeCsv(pkg.packageName)},${formatAllCounters(counters)}"
    }
    
    private fun formatClassRow(cls: ClassCoverageData): String {
        val counters = cls.counters
        val sourceFile = cls.sourceFileName ?: ""
        return "CLASS,${escapeCsv(cls.className)},${escapeCsv(sourceFile)},${formatAllCounters(counters)}"
    }
    
    private fun formatAllCounters(counters: Map<CoverageType, CoverageCounter>): String {
        val types = arrayOf(
            CoverageType.CLASS,
            CoverageType.METHOD,
            CoverageType.LINE,
            CoverageType.BRANCH,
            CoverageType.INSTRUCTION,
            CoverageType.COMPLEXITY
        )
        
        return types.joinToString(",") { type ->
            val counter = counters[type]
            if (counter != null) {
                "${counter.covered},${counter.missed},${counter.total},${CoverageFormatter.formatPercentage(counter.percentage)}"
            } else {
                "0,0,0,0.00"
            }
        }
    }
    
    private fun escapeCsv(str: String): String {
        // If the string contains comma, newline, or double quote, wrap it in quotes
        // and escape any existing double quotes by doubling them
        return if (str.contains(",") || str.contains("\n") || str.contains("\"")) {
            "\"${str.replace("\"", "\"\"")}\"" 
        } else {
            str
        }
    }
}